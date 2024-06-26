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

import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.codec.digest.Digests
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.ems.app.Ems
import org.beangle.web.action.view.{Stream, View}
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.model.{ExternSchool, Person, Project, User}
import org.openurp.base.std.model.Student
import org.openurp.code.person.model.{FamilyRelationship, Nation, PoliticalStatus}
import org.openurp.edu.grade.model.StdGpa
import org.openurp.starter.web.support.StudentSupport
import org.openurp.std.exchange.app.model.{ExchangeApply, ExchangeApplyChoice, ExchangeScheme}
import org.openurp.std.exchange.web.helper.DocHelper
import org.openurp.std.info.model.{Contact, Home, SocialRelation}

import java.io.ByteArrayInputStream
import java.time.Instant

class SignupAction extends StudentSupport with EntityAction[ExchangeApply] {

  override def projectIndex(std: Student): View = {
    val query = OqlBuilder.from(classOf[ExchangeApply], "apply")
    query.where("apply.std = :std", std)
    query.orderBy("apply.updatedAt desc")
    val applies = entityDao.search(query)
    put("applies", applies)

    val schemeQuery = OqlBuilder.from(classOf[ExchangeScheme], "s")
    schemeQuery.where("s.endAt > :now", Instant.now)
    val schemes = entityDao.search(schemeQuery)
    val avaliableSchemes = schemes.filter { s =>
      val grades = Strings.split(s.grades, ",").toSet
      grades.map(x => x.trim()).contains(std.state.get.grade)
    }

    val homes = entityDao.findBy(classOf[Home], "std", List(std))
    val home = homes.headOption.getOrElse(new Home)
    put("home", home)

    val relations = entityDao.findBy(classOf[SocialRelation], "std", List(std))
    put("relations", relations.sortBy(_.id))

    put("schemes", avaliableSchemes)
    put("std", std)
    put("stdGpa", getStdGpa(std))
    put("avatar_url", Ems.api + "/platform/user/avatars/" + Digests.md5Hex(std.code))
    put("avatar_upload_url", Ems.base + "/portal/user/avatar")
    if (applies.nonEmpty) {
      forward()
    } else {
      forward("welcome")
    }
  }

  /** 第一步编辑基本信息
   *
   * @return
   */
  def editPerson(): View = {
    val std = getStudent

    given project: Project = std.project

    val scheme = entityDao.get(classOf[ExchangeScheme], getLongId("scheme"))
    put("scheme", scheme)

    put("nations", getCodes(classOf[Nation]))
    put("politicalStatuses", getCodes(classOf[PoliticalStatus]))

    val contacts = entityDao.findBy(classOf[Contact], "std", List(std))
    put("contact", contacts.headOption.getOrElse(new Contact))
    put("std", std)
    forward()
  }

  def savePerson(): View = {
    val std = getStudent
    val person = std.person
    populate(person, "person")
    person.updatedAt = Instant.now

    val contacts = entityDao.findBy(classOf[Contact], "std", List(std))
    val user = entityDao.findBy(classOf[User], "code", std.code).head
    val contact = contacts.headOption.getOrElse(new Contact)
    populate(contact, "contact")
    contact.std = std
    contact.updatedAt = Instant.now
    user.email = contact.email
    user.mobile = contact.mobile //同步更新用户信息中的手机
    entityDao.saveOrUpdate(person, contact, user)

    redirect("editHome", "schemeId=" + getLongId("scheme"), "info.save.success")
  }

  /** 第二步编辑家庭信息
   *
   * @return
   */
  def editHome(): View = {
    val std = getStudent

    given project: Project = std.project

    val scheme = entityDao.get(classOf[ExchangeScheme], getLongId("scheme"))
    put("scheme", scheme)

    put("nations", getCodes(classOf[Nation]))
    put("politicalStatuses", getCodes(classOf[PoliticalStatus]))

    val homes = entityDao.findBy(classOf[Home], "std", List(std))
    put("home", homes.headOption.getOrElse(new Home))

    val relations = entityDao.findBy(classOf[SocialRelation], "std", List(std))
    val orderedR = relations.sortBy(_.id)
    put("relation1", orderedR.headOption.getOrElse(new SocialRelation))
    if (orderedR.size >= 2) {
      put("relation2", orderedR(1))
    } else {
      put("relation2", new SocialRelation)
    }
    put("relationships", getCodes(classOf[FamilyRelationship]))
    put("std", std)
    forward()
  }

  def saveHome(): View = {
    val std = getStudent
    val homes = entityDao.findBy(classOf[Home], "std", List(std))
    val home = homes.headOption.getOrElse(new Home)
    populate(home, "home")
    home.std = std
    home.updatedAt = Instant.now

    val relations = entityDao.findBy(classOf[SocialRelation], "std", List(std))
    val orderedR = relations.sortBy(_.id)
    val relation1 = orderedR.headOption.getOrElse(new SocialRelation)
    val relation2 = if (orderedR.size >= 2) orderedR(1) else new SocialRelation

    populate(relation1, "relation1")
    populate(relation2, "relation2")
    relation1.std = std

    entityDao.saveOrUpdate(home, relation1)

    if (Strings.isNotBlank(relation2.name) && null != relation2.relationship) {
      relation2.std = std
      entityDao.saveOrUpdate(relation2)
    }

    redirect("editApply", "schemeId=" + getLongId("scheme"), "info.save.success")
  }

  /** 第三步 编辑学籍和成绩
   *
   * @return
   */
  def editApply(): View = {
    val std = getStudent
    val schemeId = getLongId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    apply.std = std
    apply.scheme = scheme
    put("scheme", scheme)
    put("std", std)
    val stdGpa = getStdGpa(std)
    apply.gpa = stdGpa.gpa.floatValue
    apply.credits = stdGpa.credits

    put("stdGpa", stdGpa)
    put("apply", apply)
    forward()
  }

  /** 查询Gpa
   *
   * @return
   */
  private def getStdGpa(std: Student): StdGpa = {
    val stdGpas = entityDao.findBy(classOf[StdGpa], "std", List(std))
    stdGpas.headOption.getOrElse(new StdGpa)
  }

  private def getApply(std: Student, scheme: ExchangeScheme): ExchangeApply = {
    val applyQuery = OqlBuilder.from(classOf[ExchangeApply], "apply")
    applyQuery.where("apply.std  =:std", std)
    applyQuery.where("apply.scheme=:scheme", scheme)
    val applies = entityDao.search(applyQuery)
    applies.headOption.getOrElse(new ExchangeApply)
  }

  def saveApply(): View = {
    val std = getStudent
    val schemeId = getLongId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    populate(apply, "apply")
    apply.std = std
    apply.scheme = scheme
    apply.program = scheme.program
    apply.updatedAt = Instant.now
    (1 to 2) foreach { idx =>
      val schoolId = getInt(s"choice${idx}.school.id")
      val major = get(s"choice${idx}.major")
      if (schoolId.nonEmpty && major.nonEmpty) {
        val choice = apply.getChoice(idx).getOrElse(new ExchangeApplyChoice)
        choice.school = entityDao.get(classOf[ExternSchool], schoolId.get)
        choice.major = major.get
        choice.exchangeApply = apply
        choice.idx = idx
        if (!choice.persisted) apply.choices += choice
      }
    }

    val contacts = entityDao.findBy(classOf[Contact], "std", List(std))
    val contact = contacts.headOption.getOrElse(new Contact)
    val user = entityDao.findBy(classOf[User], "code", std.code).head
    apply.email = user.email.get
    apply.mobile = user.mobile.get
    apply.address = contact.address.get
    val stdGpa = getStdGpa(std)
    apply.gpa = stdGpa.gpa.floatValue()
    apply.credits = stdGpa.credits

    entityDao.saveOrUpdate(apply)
    redirect("index", "info.save.success")
  }

  def remove(): View = {
    val std = getStudent
    val schemeId = getLongId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    if (scheme.opened) {
      entityDao.remove(apply)
    }
    redirect("index", "info.remove.success")
  }

  def download(): View = {
    val std = getStudent
    val schemeId = getLongId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    val bytes = DocHelper.toDoc(apply, entityDao)
    val contentType = MediaTypes.get("docx", MediaTypes.ApplicationOctetStream).toString
    Stream(new ByteArrayInputStream(bytes), contentType, std.code + "_" + std.name + "_" + apply.scheme.program.name + "_申请表.docx")
  }
}
