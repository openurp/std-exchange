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

package org.openurp.std.exchange.web.action.std

import jakarta.servlet.http.Part
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.EmsApp
import org.beangle.web.action.annotation.mapping
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.edu.model.Course
import org.openurp.base.model.{AuditStatus, ExternSchool, Project}
import org.openurp.base.std.model.{ExternStudent, Student}
import org.openurp.code.edu.model.CourseType
import org.openurp.edu.exempt.flow.ExternExemptApply
import org.openurp.edu.exempt.model.ExternExemptCredit
import org.openurp.edu.extern.model.ExternGrade
import org.openurp.edu.grade.model.CourseGrade
import org.openurp.edu.program.domain.CoursePlanProvider
import org.openurp.edu.program.model.SharePlan
import org.openurp.edu.service.Features
import org.openurp.starter.web.support.StudentSupport

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}

class ExemptionAction extends StudentSupport with EntityAction[ExternExemptApply] {

  var coursePlanProvider: CoursePlanProvider = _

  override def projectIndex(std: Student): View = {
    val query = OqlBuilder.from(classOf[ExternStudent], "es")
    query.where("es.std = :std", std)
    query.orderBy("es.beginOn")
    val externStudents = entityDao.search(query)
    put("externStudents", externStudents)

    given project: Project = std.project

    val exemptionCredit = entityDao.findBy(classOf[ExternExemptCredit], "std", std).headOption
    put("exemptionCredit", exemptionCredit)
    if (externStudents.nonEmpty) {
      //find apply
      val applyQuery = OqlBuilder.from(classOf[ExternExemptApply], "apply")
      applyQuery.where("apply.externStudent in(:ess)", externStudents)
      applyQuery.orderBy("apply.externStudent.beginOn")
      val applies = entityDao.search(applyQuery)
      put("applies", applies.map(x => (x.externStudent, x)).toMap)

      //find grades
      val gradeQuery = OqlBuilder.from(classOf[ExternGrade], "eg")
        .where("eg.externStudent in(:ess)", externStudents)
      val grades = entityDao.search(gradeQuery)
      val gradeMap = grades.groupBy(_.externStudent)
      put("gradeMap", gradeMap)

      put("scoreNeeded", getConfig(Features.Exempt.ScoreNeeded))
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
    val std = getStudent
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
    val std = getStudent
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

    val rs = populateEntity(classOf[ExternStudent], "externStudent")
    rs.std = std
    rs.updatedAt = Instant.now
    rs.school = school

    val q = OqlBuilder.from(classOf[ExternStudent], "es");
    q.where("es.std=:std", std)
    q.where("es.school=:school", school)
    q.where("to_char(es.beginOn,'yyyyMM')=:beginOn", rs.beginOn.format(DateTimeFormatter.ofPattern("yyyyMM")))
    val exists = entityDao.search(q)

    if (exists.nonEmpty) {
      val apply = getApply(exists.head)
      if (null != apply && apply.status == AuditStatus.Passed) {
        return redirect("index", "已经存在同样的申请了")
      }
    }
    entityDao.saveOrUpdate(rs)
    redirect("editGrades", "&externStudent.id=" + rs.id, "info.save.success")
  }

  /** 第二步 编辑成绩，保存附件
   *
   * @return
   */
  def editGrades(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], getLongId("externStudent"))

    given project: Project = externStudent.std.project

    //find grades
    val gradeQuery = OqlBuilder.from(classOf[ExternGrade], "eg")
      .where("eg.externStudent  =:es", externStudent)
    val grades = entityDao.search(gradeQuery)
    put("grades", grades)
    put("externStudent", externStudent)

    put("scoreNeeded", getConfig(Features.Exempt.ScoreNeeded))
    put("apply", getApply(externStudent))
    forward();
  }

  /** 保存第二步
   *
   * @return
   */
  def saveGrades(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], getLongId("externStudent"))
    val std = externStudent.std
    val apply = getApply(externStudent)
    val grades = Collections.newBuffer[ExternGrade]
    var credits = 0f
    (1 to 30) foreach { i =>
      val grade = populateEntity(classOf[ExternGrade], "grade_" + i)
      if (Strings.isNotEmpty(grade.courseName) && null != grade.acquiredOn) {
        grade.externStudent = externStudent
        if Strings.isBlank(grade.scoreText) then grade.scoreText = "--"
        credits += grade.credits
        grade.status = AuditStatus.Submited
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
      val meta = repo.upload("/exchange/transcript", part.getInputStream,
        std.code + "_" + part.getSubmittedFileName, std.code + " " + std.name);
      apply.transcriptPath = Some(meta.filePath)
    }

    apply.updatedAt = Instant.now()
    entityDao.saveOrUpdate(apply)
    redirect("editApplies", "&externStudent.id=" + externStudent.id, "info.save.success")
  }

  private def getApply(externStudent: ExternStudent): ExternExemptApply = {
    val q = OqlBuilder.from(classOf[ExternExemptApply], "apply")
    q.where("apply.externStudent =:es", externStudent)
    val applies = entityDao.search(q)
    val apply = applies.headOption.getOrElse(new ExternExemptApply)
    if (null == apply.semester) {
      apply.semester = semesterService.get(externStudent.std.project, LocalDate.now)
    }
    apply.externStudent = externStudent
    apply
  }

  /** 第三步编辑免修关系
   *
   * @return
   */
  def editApplies(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], getLongId("externStudent"))
    val apply = getApply(externStudent)
    put("externStudent", externStudent)
    put("apply", apply)
    entityDao.findBy(classOf[ExternExemptCredit], "std", List(apply.externStudent.std)) foreach { e =>
      put("exemptionCredit", e)
    }
    //find grades
    val gradeQuery = OqlBuilder.from(classOf[ExternGrade], "eg")
      .where("eg.externStudent  =:es", externStudent)
    val grades = entityDao.search(gradeQuery)
    put("grades", grades)
    put("planCourses", getPlanCourses(externStudent.std))
    forward()
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
          if (!group.autoAddup) {
            emptyCourseTypes += group.courseType
          }
        }
      }
    }
    val spQuery = OqlBuilder.from(classOf[SharePlan], "sp")
    spQuery.where("sp.project=:project", std.project)
    spQuery.where("sp.level=:level and sp.eduType =:eduType", std.level, std.eduType)
    spQuery.where(":grade between sp.fromGrade.code and sp.toGrade.code", std.state.get.grade.code)
    entityDao.search(spQuery) foreach { sp =>
      for (cg <- sp.groups; planCourse <- cg.planCourses) {
        courses.add(planCourse.course)
      }
      sp.groups foreach { cg => emptyCourseTypes.subtractOne(cg.courseType) }
    }
    if (emptyCourseTypes.nonEmpty) {
      val typeQuery = OqlBuilder.from(classOf[CourseType], "ct").where("ct.parent in(:parents)", emptyCourseTypes)
      emptyCourseTypes ++= entityDao.search(typeQuery)
      val q = OqlBuilder.from(classOf[Course], "c")
      q.where("c.project=:project", std.project)
      q.where("c.endOn is null and c.courseType in(:courseTypes)", emptyCourseTypes)
      courses.addAll(entityDao.search(q))
    }
    val query = OqlBuilder.from[Course](classOf[CourseGrade].getName, "cg")
    query.where("cg.std=:std and cg.passed=true", std)
    query.select("cg.course")
    val hasGrade = entityDao.search(query)

    courses.subtractAll(hasGrade)
    courses.toBuffer
  }

  /** 保存第三步
   *
   * @return
   */
  def saveApplies(): View = {
    val externStudent = entityDao.get(classOf[ExternStudent], getLongId("externStudent"))
    val apply = getApply(externStudent)
    if (apply.status == AuditStatus.Passed) {
      return redirect("index", "已经审核通过的申请不能再次冲抵")
    }
    val courseSet = Collections.newSet[Course]
    val grades = entityDao.findBy(classOf[ExternGrade], "externStudent", List(apply.externStudent))
    grades foreach { m =>
      val courses = getAll(s"grade_${m.id}.courses") map (x => entityDao.get(classOf[Course], x.toString.toLong))
      m.exempts.clear()
      courseSet.addAll(courses)
      m.exempts.addAll(courses)
    }
    apply.updatedAt = Instant.now
    val std = apply.externStudent.std
    apply.exemptionCredits = courseSet.toSeq.map(_.defaultCredits).sum
    entityDao.saveOrUpdate(apply)
    val limit = entityDao.findBy(classOf[ExternExemptCredit], "std", List(std)).headOption
    limit match {
      case None => apply.status = AuditStatus.Submited
      case Some(l) =>
        val totalCredits = entityDao.findBy(classOf[ExternExemptApply], "externStudent.std", List(std)).map(_.exemptionCredits).sum
        apply.auditOpinion = None
        if (l.maxValue == 0 || java.lang.Float.compare(l.maxValue, totalCredits) >= 0) {
          apply.status = AuditStatus.Submited
        } else {
          apply.status = AuditStatus.Draft
        }
    }
    entityDao.saveOrUpdate(apply)
    if (apply.status == AuditStatus.Submited) {
      redirect("index", "info.save.success")
    } else {
      redirect("editApplies", "&externStudent.id=" + apply.externStudent.id, "超出认定学分上限，请重新选择课程")
    }
  }

  @mapping(value = "new", view = "new,form")
  def editNew(): View = {
    val entity = getEntity(entityClass, simpleEntityName)
    put(simpleEntityName, entity)
    forward()
  }

  @mapping(method = "delete")
  def remove(): View = {
    val std = getStudent

    val applyId = getLong("apply.id")
    applyId match {
      case Some(id) =>
        val ea = entityDao.get(classOf[ExternExemptApply], id)
        if (ea.externStudent.std == std && ea.status != AuditStatus.Passed) {
          ea.transcriptPath foreach { p =>
            try {
              EmsApp.getBlobRepository(true).remove(p)
            } catch
              case e: Throwable =>
          }
          entityDao.remove(ea)
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
              val grades = entityDao.findBy(classOf[ExternGrade], "externStudent", List(apply.externStudent))
              entityDao.remove(grades)
              entityDao.remove(externStudent)
              redirect("index", "info.remove.success")
            }
          case None => redirect("index", "缺少参数")
        }
    }
  }
}
