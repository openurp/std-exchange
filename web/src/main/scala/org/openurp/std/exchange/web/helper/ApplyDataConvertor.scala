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

package org.openurp.std.exchange.web.helper

import org.beangle.commons.collection.Collections
import org.openurp.std.exchange.app.model.ExchangeApply
import org.openurp.std.info.model.{Home, SocialRelation}

import java.time.{LocalDateTime, ZoneId}

object ApplyDataConvertor {

  import java.text.NumberFormat

  val nf = NumberFormat.getNumberInstance
  nf.setMinimumFractionDigits(2)
  nf.setMaximumFractionDigits(2)

  def convert(apply: ExchangeApply, home: Home, relations: Seq[SocialRelation]): collection.Map[String, String] = {
    val data = Collections.newMap[String, String]
    val user = apply.std.user
    data.put("user_code", user.code)
    data.put("user_name", user.name)
    data.put("user_school_name", user.school.name)

    val state = apply.std.state.get
    data.put("state_department_name", state.department.name)
    data.put("state_major_grade", state.major.name + "、" + state.grade)

    val person = apply.std.person
    data.put("person_gender_name", person.gender.name)
    data.put("person_birthday", person.birthday.toString)
    data.put("person_nation_name", person.nation.map(_.name).getOrElse(""))
    data.put("person_politicalStatus_name", person.politicalStatus.map(_.name).getOrElse(""))
    data.put("person_homeTown", person.homeTown.getOrElse(""))
    data.put("person_birthplace", person.birthplace.getOrElse(""))
    data.put("person_code", person.code)

    apply.getChoice(1) match {
      case Some(c) => data.put("choice1", c.school.name + " " + c.major)
      case None => data.put("choice1", "")
    }
    apply.getChoice(2) match {
      case Some(c) => data.put("choice2", c.school.name + " " + c.major)
      case None => data.put("choice2", "")
    }

    data.put("apply_gpa", nf.format(apply.gpa))
    data.put("apply_credits", apply.credits.toString)
    data.put("apply_rankInMajor", apply.rankInMajor)
    data.put("apply_statements", apply.statements)
    data.put("apply_email", apply.email)
    data.put("apply_mobile", apply.mobile)
    data.put("apply_address", apply.address)

    val r1 = relations.headOption.getOrElse(new SocialRelation)
    val r2 = if (relations.size >= 2) relations(1) else new SocialRelation
    data.put("r1_name", r1.name)
    data.put("r1_ship", if (null == r1.relationship) "" else r1.relationship.name)
    data.put("r1_duty", r1.duty.getOrElse(""))
    data.put("r1_phone", r1.phone.getOrElse(""))

    data.put("r2_name", r2.name)
    data.put("r2_ship", if (null == r2.relationship) "" else r2.relationship.name)
    data.put("r2_duty", r2.duty.getOrElse(""))
    data.put("r2_phone", r2.phone.getOrElse(""))

    data.put("home_address", home.address.getOrElse(""))
    data.put("program",apply.program.name)
    val updatedAt=LocalDateTime.ofInstant(apply.updatedAt,ZoneId.systemDefault())
    data.put("apply_at_year",updatedAt.getYear.toString)
    data.put("apply_at_month",updatedAt.getMonthValue.toString)
    data.put("apply_at_day",updatedAt.getDayOfMonth.toString)
    data
  }
}
