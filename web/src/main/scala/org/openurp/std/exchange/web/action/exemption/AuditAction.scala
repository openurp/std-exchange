/*
 * Copyright (C) 2005, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.std.exchange.web.action.exemption

import org.beangle.commons.collection.{Collections, Properties}
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.EmsApp
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.edu.AuditStates
import org.openurp.base.edu.code.model.CourseType
import org.openurp.base.edu.model.{Course, Semester, Student}
import org.openurp.base.model.ExternSchool
import org.openurp.code.edu.model.GradingMode
import org.openurp.code.std.model.StudentStatus
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.exchange.model.{ExchangeGrade, ExemptionApply}
import org.openurp.std.exchange.service.{ExemptionCourse, ExemptionService}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AuditAction extends RestfulAction[ExemptionApply] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  var exemptionService: ExemptionService = _

  override def indexSetting(): Unit = {
    put("studentStatuses", getCodes(classOf[StudentStatus]))
    put("auditStates", AuditStates.values)
  }

  override def info(id: String): View = {
    val apply = getModel[ExemptionApply](entityName, convertId(id))
    val repo = EmsApp.getBlobRepository(true)
    apply.transcriptPath foreach { p =>
      put("transcriptPath", repo.url(p))
    }
    put("apply", apply)
    val grades = entityDao.findBy(classOf[ExchangeGrade], "externStudent", List(apply.externStudent))
    put("grades", grades)
    forward()
  }

  override protected def editSetting(entity: ExemptionApply): Unit = {
    put("schools", entityDao.getAll(classOf[ExternSchool]))
    val project = getProject
    put("levels", project.levels)
    put("categories",List(project.category))
    put("project", project)
    super.editSetting(entity)
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[Student], "std")
    query.where("std.user.code=:code", get("q", ""))
    entityDao.search(query).map { std =>
      val p = new Properties()
      p.put("id", std.id)
      p.put("name", s"${std.state.get.department.name} ${std.user.name}")
      p
    }
  }

  private def getSemester(date: LocalDate): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
      .where("semester.calendar in(:calendars)", getProject.calendars)
    builder.where("semester.endOn > :date", date)
    builder.orderBy("semester.beginOn")
    builder.limit(1, 1)
    entityDao.search(builder).head
  }

  private def buildCourseTypes(std: Student): Map[Course, CourseType] = {
    coursePlanProvider.getCoursePlan(std) match {
      case None => Map.empty
      case Some(plan) =>
        val courseTypes = Collections.newMap[Course, CourseType]
        for (cg <- plan.groups) {
          cg.planCourses foreach { x => courseTypes.put(x.course, x.group.courseType) }
        }
        courseTypes.toMap
    }
  }

  def audit(): View = {
    val esId = longId("apply")
    val auditOpinion = get("auditOpinion")
    val apply = entityDao.get(classOf[ExemptionApply], esId)
    val passed = getBoolean("passed", false)
    val gradeQuery = OqlBuilder.from(classOf[ExchangeGrade], "eg")
      .where("eg.externStudent=:es", apply.externStudent)
    val grades = entityDao.search(gradeQuery)
    apply.auditOpinion = auditOpinion
    if (passed) {
      apply.auditState = AuditStates.Finalized
      val courseTypes = buildCourseTypes(apply.externStudent.std)
      val allCourses = Collections.newSet[Course]
      grades foreach { eg =>
        val ecs = Collections.newBuffer[ExemptionCourse]
        val semester = getSemester(eg.acquiredOn)
        val gradingMode = entityDao.get(classOf[GradingMode], GradingMode.Percent)
        eg.courses foreach { c =>
          if (!allCourses.contains(c)) {
            val ec = ExemptionCourse(c, courseTypes.getOrElse(c, c.courseType), semester, c.examMode, gradingMode, None, None)
            ecs += ec
            allCourses += c
          }
        }
        if (ecs.nonEmpty) {
          exemptionService.addExemption(eg, ecs.toSeq)
        }
      }
    } else {
      apply.auditState = AuditStates.Rejected
      grades foreach { g =>
        g.auditState = apply.auditState
      }
    }
    entityDao.saveOrUpdate(apply)
    entityDao.saveOrUpdate(grades)
    redirect("search", "info.save.success")
  }

  override protected def simpleEntityName: String = {
    "apply"
  }
}
