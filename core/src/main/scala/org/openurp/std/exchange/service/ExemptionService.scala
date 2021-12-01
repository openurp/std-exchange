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

package org.openurp.std.exchange.service

import java.time.LocalDate
import org.openurp.code.edu.model.{ExamMode, GradingMode}
import org.openurp.base.edu.code.model.CourseType
import org.openurp.base.edu.model.{Course, Semester, Student}
import org.openurp.std.exchange.model.{ExchangeGrade}
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.program.model.{CoursePlan, PlanCourse, Program}

trait ExemptionService {

  def getSemester(program:Program, acquiredOn: LocalDate, term: Option[Int]): Option[Semester]

  def getConvertablePlanCourses(std: Student, plan: CoursePlan, acquiredOn: LocalDate): Seq[PlanCourse]

  def getConvertedGrades(std: Student, courses: collection.Iterable[Course]): Seq[CourseGrade]

  def recalcExemption(std: Student): Unit

  def removeExemption(eg: ExchangeGrade, course: Course): Unit

  def addExemption(eg: ExchangeGrade, ecs: Seq[ExemptionCourse]): Unit

}

case class ExemptionCourse(course: Course, courseType: CourseType, semester: Semester,
                           examMode: ExamMode, gradingMode: GradingMode, score: Option[Float], scoreText: Option[String])
