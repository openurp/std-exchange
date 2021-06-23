/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.std.exchange.web.action.std

import jakarta.servlet.http.Part
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.EmsApp
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.base.edu.AuditStates
import org.openurp.base.edu.code.model.CourseType
import org.openurp.base.edu.model.{Course, ExternStudent, Student}
import org.openurp.base.model.ExternSchool
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.exchange.model.{ExchangeGrade, ExemptionApply, ExemptionCredit}

import java.time.{Instant, LocalDate}

class ExemptionAction extends RestfulAction[ExemptionApply] with ProjectSupport {

  var coursePlanProvider: CoursePlanProvider = _

  override def index(): View = {
    put("projects", getUserProjects(classOf[Student]))
    val std = getUser(classOf[Student])

    val query = OqlBuilder.from(classOf[ExternStudent], "es")
    query.where("es.std = :std", std)
    query.orderBy("es.beginOn")
    val externStudents = entityDao.search(query)
    put("externStudents", externStudents)

    if (externStudents.nonEmpty) {
      //find apply
      val applyQuery = OqlBuilder.from(classOf[ExemptionApply], "apply")
      applyQuery.where("apply.externStudent in(:ess)", externStudents)
      applyQuery.orderBy("apply.externStudent.beginOn")
      val applies = entityDao.search(applyQuery)
      put("applies", applies.map(x => (x.externStudent, x)).toMap)

      //find grades
      val gradeQuery = OqlBuilder.from(classOf[ExchangeGrade], "eg")
        .where("eg.externStudent in(:ess)", externStudents)
      val grades = entityDao.search(gradeQuery)
      val gradeMap = grades.groupBy(_.externStudent)
      put("gradeMap", gradeMap)

      //find transcript
      val repo = EmsApp.getBlobRepository(true)
      val paths = applies.filter(_.transcriptPath.isDefined).map(x => (x, repo.url(x.transcriptPath.get)))
      put("transcriptPaths", paths.toMap)
      forward()
    } else {
      put("applies", Map.empty)
      forward("welcome")
    }
  }

  /** 第一步,修改校外学习经历 */
  def editExternStudent(): View = {
    val std = getUser(classOf[Student])
    val externStudent = getLong("externStudent.id") match {
      case Some(id) => entityDao.get(classOf[ExternStudent], id)
      case None => val ns = new ExternStudent
        ns.std = std
        ns
    }
    put("externStudent", externStudent)
    put("std", std)
    put("schools", entityDao.getAll(classOf[ExternSchool]))
    put("levels", List(std.level))
    put("eduCategories", List(std.project.category))
    forward()
  }

  /** 保存第一步 */
  def saveExternStudent(): View = {
    val std = getUser(classOf[Student])
    var school: ExternSchool = null
    val schoolId = getInt("externStudent.school.id")
    schoolId match {
      case Some(sid) => school = entityDao.get(classOf[ExternSchool], sid.toInt)
      case None =>
        get("newSchool") foreach { nsname =>
          val schools = entityDao.findBy(classOf[ExternSchool], "name", List(nsname))
          if (schools.nonEmpty) {
            school = schools.head
          } else {
            school = new ExternSchool
            school.name = nsname
            school.beginOn = LocalDate.now
            school.updatedAt = Instant.now
            school.code = "user_add_" + System.currentTimeMillis()
            entityDao.saveOrUpdate(school)
          }
        }
    }

    val externStudent = populateEntity(classOf[ExternStudent], "externStudent")
    externStudent.std = std
    externStudent.updatedAt = Instant.now
    entityDao.saveOrUpdate(externStudent)
    redirect("editGrades", "&externStudent.id=" + externStudent.id, "info.save.success")
  }

  /** 第二步 编辑成绩，保存附件
   *
   * @return
   */
  def editGrades(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], longId("externStudent"))

    //find grades
    val gradeQuery = OqlBuilder.from(classOf[ExchangeGrade], "eg")
      .where("eg.externStudent  =:es", externStudent)
    val grades = entityDao.search(gradeQuery)
    put("grades", grades)
    put("externStudent", externStudent)

    put("apply", getApply(externStudent))
    forward();
  }

  /** 保存第二步
   *
   * @return
   */
  def saveGrades(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], longId("externStudent"))
    val std = externStudent.std
    val apply = getApply(externStudent)
    val grades = Collections.newBuffer[ExchangeGrade]
    var credits = 0f
    (1 to 30) foreach { i =>
      val grade = populateEntity(classOf[ExchangeGrade], "grade_" + i)
      if (Strings.isNotEmpty(grade.courseName) && Strings.isNotEmpty(grade.scoreText) && null != grade.acquiredOn) {
        grade.externStudent = externStudent
        credits += grade.credits
        grade.updatedAt = Instant.now
        grades += grade
      }
    }
    entityDao.saveOrUpdate(grades)
    apply.credits = credits

    //process file
    val parts = getAll("transcript", classOf[Part])
    if (parts.nonEmpty && parts.head.getSize > 0) {
      val repo = EmsApp.getBlobRepository(true)
      val part = parts.head
      apply.transcriptPath foreach { p =>
        repo.remove(p)
      }
      val meta = repo.upload("/transcript", part.getInputStream,
        std.user.code + "_" + part.getSubmittedFileName, std.user.code + " " + std.user.name);
      apply.transcriptPath = Some(meta.filePath)
    }

    apply.updatedAt = Instant.now()
    entityDao.saveOrUpdate(apply)
    redirect("editApplies", "&externStudent.id=" + externStudent.id, "info.save.success")
  }


  /** 第三步编辑免修关系
   *
   * @return
   */
  def editApplies(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], longId("externStudent"))
    val apply = getApply(externStudent)
    put("externStudent", externStudent)
    put("apply", apply)
    entityDao.findBy(classOf[ExemptionCredit], "std", List(apply.externStudent.std)) foreach { e =>
      put("exemptionCredit", e)
    }
    //find grades
    val gradeQuery = OqlBuilder.from(classOf[ExchangeGrade], "eg")
      .where("eg.externStudent  =:es", externStudent)
    val grades = entityDao.search(gradeQuery)
    put("grades", grades)
    put("planCourses", getPlanCourses(apply.externStudent.std))
    forward()
  }

  /** 保存第三步
   *
   * @return
   */
  def saveApplies(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], longId("externStudent"))
    val apply = getApply(externStudent)
    if (apply.auditState == AuditStates.Accepted) {
      return redirect("index", "已经审核通过的申请不能再次冲抵")
    }
    val courseSet = Collections.newSet[Course]
    val grades = entityDao.findBy(classOf[ExchangeGrade], "externStudent", List(apply.externStudent))
    grades foreach { m =>
      val courses = getAll(s"grade_${m.id}.courses") map (x => entityDao.get(classOf[Course], x.toString.toLong))
      m.courses.clear()
      courseSet.addAll(courses)
      m.courses.addAll(courses)
    }
    apply.updatedAt = Instant.now
    val std = apply.externStudent.std
    apply.exemptionCredits = courseSet.toSeq.map(_.credits).sum
    entityDao.saveOrUpdate(apply)
    val limit = entityDao.findBy(classOf[ExemptionCredit], "std", List(std)).headOption
    limit match {
      case None => apply.auditState = AuditStates.Submited
      case Some(l) =>
        val totalCredits = entityDao.findBy(classOf[ExemptionApply], "externStudent.std", List(std)).map(_.exemptionCredits).sum
        if (l.maxValue == 0 || java.lang.Float.compare(l.maxValue, totalCredits) >= 0) {
          apply.auditState = AuditStates.Submited
        } else {
          apply.auditState = AuditStates.Draft
        }
    }
    entityDao.saveOrUpdate(apply)
    if (apply.auditState == AuditStates.Submited) {
      redirect("index", "info.save.success")
    } else {
      redirect("editApplies", "&externStudent.id=" + apply.id, "超出认定学分上限，请重新选择课程")
    }
  }


  @mapping(method = "delete")
  override def remove(): View = {
    val std = getUser(classOf[Student])

    val applyId = getLong("apply.id")
    applyId match {
      case Some(id) =>
        val es = entityDao.get(classOf[ExemptionApply], id)
        if (es.externStudent.std == std && es.auditState != AuditStates.Accepted) {
          es.transcriptPath foreach { p =>
            EmsApp.getBlobRepository(true).remove(p)
          }
          entityDao.remove(es)
          redirect("index", "info.remove.success")
        } else {
          redirect("index", "删除失败")
        }
      case None =>
        val externStudentId = getLong("externStudent.id")
        externStudentId match {
          case Some(id) =>
            val externStudent = entityDao.get(classOf[ExternStudent], id)
            val apply = getApply(externStudent)
            if (apply.persisted) {
              redirect("index", "请先删除冲抵申请")
            } else {
              val grades = entityDao.findBy(classOf[ExchangeGrade], "externStudent", List(apply.externStudent))
              entityDao.remove(grades)
              entityDao.remove(externStudent)
              redirect("index", "info.remove.success")
            }
          case None => redirect("index", "缺少参数")
        }
    }
  }

  private def getPlanCourses(std: Student): collection.Seq[Course] = {
    val courses = Collections.newSet[Course]
    val emptyCourseTypes = Collections.newSet[CourseType]
    coursePlanProvider.getCoursePlan(std) foreach { plan =>
      for (group <- plan.groups) {
        if (group.planCourses.isEmpty && group.children.isEmpty) {
          emptyCourseTypes += group.courseType
        } else {
          for (planCourse <- group.planCourses) {
            courses.addOne(planCourse.course)
          }
        }
      }
    }

    if (emptyCourseTypes.nonEmpty) {
      val typeQuery = OqlBuilder.from(classOf[CourseType], "ct").where("ct.parent in(:parents)", emptyCourseTypes)
      emptyCourseTypes ++= entityDao.search(typeQuery)
      courses.addAll(entityDao.findBy(classOf[Course], "courseType", emptyCourseTypes))
    }

    val query = OqlBuilder.from[Course](classOf[CourseGrade].getName, "cg")
    query.where("cg.std=:std and cg.passed=true", std)
    query.select("cg.course")
    courses.subtractAll(entityDao.search(query))
    courses.toBuffer
  }

  private def getApply(externStudent: ExternStudent): ExemptionApply = {
    val applyQuery = OqlBuilder.from(classOf[ExemptionApply], "apply")
    applyQuery.where("apply.externStudent  =:es", externStudent)
    val applies = entityDao.search(applyQuery)
    val apply = applies.headOption.getOrElse(new ExemptionApply)
    apply.externStudent = externStudent
    apply
  }
}
