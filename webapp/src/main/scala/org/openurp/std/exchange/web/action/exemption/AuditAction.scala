/*
 * Copyright (C) 2014, The OpenURP Software.
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
import org.openurp.base.edu.model.Course
import org.openurp.base.model.{AuditStatus, ExternSchool, Project, Semester}
import org.openurp.base.std.model.Student
import org.openurp.code.edu.model.CourseType
import org.openurp.code.std.model.StudentStatus
import org.openurp.edu.exempt.flow.ExternExemptApply
import org.openurp.edu.extern.model.ExternGrade
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.service.Features
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.exchange.service.ExchangeService

import java.time.LocalDate

class AuditAction extends RestfulAction[ExternExemptApply] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  var exemptionService: ExchangeService = _

  override def indexSetting(): Unit = {
    given project: Project = getProject

    put("studentStatuses", getCodes(classOf[StudentStatus]))
  }

  override def info(id: String): View = {
    val apply = entityDao.get(classOf[ExternExemptApply], id.toLong)
    val repo = EmsApp.getBlobRepository(true)
    apply.transcriptPath foreach { p =>
      put("transcriptPath", repo.url(p))
    }

    given project: Project = apply.externStudent.std.project

    put("apply", apply)
    put("scoreNeeded", getConfig(Features.Exempt.ScoreNeeded))
    val grades = entityDao.findBy(classOf[ExternGrade], "externStudent", List(apply.externStudent))
    put("grades", grades)
    forward()
  }

  override protected def editSetting(entity: ExternExemptApply): Unit = {
    put("schools", entityDao.getAll(classOf[ExternSchool]))
    val project = getProject
    put("levels", project.levels)
    put("categories", List(project.category))
    put("project", project)
    super.editSetting(entity)
  }

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[Student], "std")
    query.where("std.code=:code", get("q", ""))
    entityDao.search(query).map { std =>
      val p = new Properties()
      p.put("id", std.id)
      p.put("name", s"${std.state.get.department.name} ${std.name}")
      p
    }
  }

  private def getSemester(date: LocalDate): Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
      .where("semester.calendar =:calendar", getProject.calendar)
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
    val esId = getLongId("apply")
    val auditOpinion = get("auditOpinion")
    val apply = entityDao.get(classOf[ExternExemptApply], esId)
    val passed = getBoolean("passed", false)
    val gradeQuery = OqlBuilder.from(classOf[ExternGrade], "eg")
      .where("eg.externStudent=:es", apply.externStudent)
    val grades = entityDao.search(gradeQuery)
    apply.auditOpinion = auditOpinion
    if (passed) {
      apply.status = AuditStatus.Passed
      val courseTypes = buildCourseTypes(apply.externStudent.std)
      val allCourses = Collections.newSet[Course]
      grades foreach { eg =>
        val courses = Collections.newSet[Course]
        eg.exempts foreach { c =>
          if !allCourses.contains(c) then
            courses += c
            allCourses += c
        }
        if (courses.nonEmpty) {
          exemptionService.addExemption(eg, courses, None)
        }
      }
    } else {
      apply.status = AuditStatus.Rejected
      grades foreach { g =>
        g.status = apply.status
      }
      val courses = grades.flatMap(_.exempts)
      val converted = exemptionService.getConvertedGrades(apply.externStudent.std, courses)
      entityDao.remove(converted)
    }
    entityDao.saveOrUpdate(apply)
    entityDao.saveOrUpdate(grades)
    exemptionService.recalcExemption(apply.externStudent.std)
    redirect("search", "info.save.success")
  }

  override protected def simpleEntityName: String = {
    "apply"
  }
}
