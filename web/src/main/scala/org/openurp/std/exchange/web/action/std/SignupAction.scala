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

import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.codec.digest.Digests
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.Ems
import org.beangle.webmvc.api.view.{Stream, View}
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.base.edu.model.Student
import org.openurp.base.model.{ExternSchool, Person}
import org.openurp.code.person.model.{FamilyRelationship, Nation, PoliticalStatus}
import org.openurp.edu.grade.course.model.StdGpa
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.exchange.app.model.{ExchangeApply, ExchangeApplyChoice, ExchangeScheme}
import org.openurp.std.exchange.web.helper.DocHelper
import org.openurp.std.info.model.{Contact, Home, SocialRelation}

import java.io.ByteArrayInputStream
import java.time.Instant

class SignupAction extends EntityAction[ExchangeApply] with ProjectSupport {

  def index(): View = {
    put("projects", getUserProjects(classOf[Student]))
    val std = getUser(classOf[Student])

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
    put("stdGpa",getStdGpa(std))
    put("avatar_url", Ems.api + "/platform/user/avatars/" + Digests.md5Hex(std.user.code))
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
    val std = getUser(classOf[Student])
    val scheme = entityDao.get(classOf[ExchangeScheme], longId("scheme"))
    put("scheme", scheme)

    put("nations", getCodes(classOf[Nation]))
    put("politicalStatuses", getCodes(classOf[PoliticalStatus]))

    val contacts = entityDao.findBy(classOf[Contact], "std", List(std))
    put("contact", contacts.headOption.getOrElse(new Contact))
    put("std", std)
    forward()
  }

  def savePerson(): View = {
    val std = getUser(classOf[Student])
    val person = std.person
    populate(person, classOf[Person].getName, "person")
    person.updatedAt = Instant.now

    val contacts = entityDao.findBy(classOf[Contact], "std", List(std))
    val contact = contacts.headOption.getOrElse(new Contact)
    populate(contact, classOf[Contact].getName, "contact")
    contact.std = std
    contact.updatedAt = Instant.now
    std.user.email = contact.email
    std.user.mobile = contact.mobile //同步更新用户信息中的手机
    entityDao.saveOrUpdate(person, contact, std.user)

    redirect("editHome", "schemeId=" + longId("scheme"), "info.save.success")
  }

  /** 第二步编辑家庭信息
   *
   * @return
   */
  def editHome(): View = {
    val std = getUser(classOf[Student])
    val scheme = entityDao.get(classOf[ExchangeScheme], longId("scheme"))
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
    val std = getUser(classOf[Student])
    val homes = entityDao.findBy(classOf[Home], "std", List(std))
    val home = homes.headOption.getOrElse(new Home)
    populate(home, classOf[Home].getName, "home")
    home.std = std
    home.updatedAt = Instant.now

    val relations = entityDao.findBy(classOf[SocialRelation], "std", List(std))
    val orderedR = relations.sortBy(_.id)
    val relation1 = orderedR.headOption.getOrElse(new SocialRelation)
    val relation2 = if (orderedR.size >= 2) orderedR(1) else new SocialRelation

    populate(relation1, classOf[SocialRelation].getName, "relation1")
    populate(relation2, classOf[SocialRelation].getName, "relation2")
    relation1.std = std

    entityDao.saveOrUpdate(home, relation1)

    if (Strings.isNotBlank(relation2.name) && null != relation2.relationship) {
      relation2.std = std
      entityDao.saveOrUpdate(relation2)
    }

    redirect("editApply", "schemeId=" + longId("scheme"), "info.save.success")
  }

  /** 第三步 编辑学籍和成绩
   *
   * @return
   */
  def editApply(): View = {
    val std = getUser(classOf[Student])
    val schemeId = longId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    apply.std = std
    apply.scheme = scheme
    put("scheme", scheme)
    put("std", std)
    val stdGpa = getStdGpa(std)
    apply.gpa = stdGpa.gpa
    apply.credits = stdGpa.credits

    put("stdGpa",stdGpa)
    put("apply", apply)
    forward()
  }

  def saveApply(): View = {
    val std = getUser(classOf[Student])
    val schemeId = longId("scheme")
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
        choice.apply = apply
        choice.idx = idx
        if (!choice.persisted) apply.choices += choice
      }
    }

    val contacts = entityDao.findBy(classOf[Contact], "std", List(std))
    val contact = contacts.headOption.getOrElse(new Contact)
    apply.email = std.user.email.get
    apply.mobile = std.user.mobile.get
    apply.address = contact.address.get
    val stdGpa= getStdGpa(std)
    apply.gpa = stdGpa.gpa
    apply.credits = stdGpa.credits

    entityDao.saveOrUpdate(apply)
    redirect("index", "info.save.success")
  }

  def remove(): View = {
    val std = getUser(classOf[Student])
    val schemeId = longId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    if (scheme.opened) {
      entityDao.remove(apply)
    }
    redirect("index", "info.remove.success")
  }

  def download(): View = {
    val std = getUser(classOf[Student])
    val schemeId = longId("scheme")
    val scheme = entityDao.get(classOf[ExchangeScheme], schemeId)
    val apply = getApply(std, scheme)
    val bytes = DocHelper.toDoc(apply, entityDao)
    val contentType = MediaTypes.get("docx", MediaTypes.ApplicationOctetStream).toString
    Stream(new ByteArrayInputStream(bytes), contentType, std.user.code + "_" + std.user.name + "_" + apply.scheme.program.name + "_申请表.docx")
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
}
