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

package org.openurp.std.exchange.web.action.signup

import org.beangle.commons.activation.MediaTypes
import org.beangle.commons.codec.digest.Digests
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.Ems
import org.beangle.webmvc.annotation.{mapping, param}
import org.beangle.webmvc.view.{Stream, View}
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.ExternSchool
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.exchange.app.model.{ExchangeApply, ExchangeScheme}
import org.openurp.std.exchange.web.helper.DocHelper
import org.openurp.std.info.model.{Home, SocialRelation}

import java.io.ByteArrayInputStream

class ApplyAction extends RestfulAction[ExchangeApply] with ProjectSupport {
  override protected def indexSetting(): Unit = {
    put("schemes", entityDao.getAll(classOf[ExchangeScheme]).sortBy(_.beginAt))
    put("schools", entityDao.getAll(classOf[ExternSchool]))
  }

  override protected def getQueryBuilder: OqlBuilder[ExchangeApply] = {
    val builder = super.getQueryBuilder
    getInt("school.id") foreach { schoolId =>
      builder.where("exists(from exchangeApply.choices as c where c.school.id=:schoolId)", schoolId)
    }
    builder
  }

  @mapping(value = "{id}")
  override def info(@param("id") id: String): View = {
    val lid = id.toLong
    val apply = entityDao.get(classOf[ExchangeApply], lid)
    val relations = entityDao.findBy(classOf[SocialRelation], "std", List(apply.std))
    put("relations", relations.sortBy(_.id))

    val homes = entityDao.findBy(classOf[Home], "std", List(apply.std))
    val home = homes.headOption.getOrElse(new Home)
    put("home", home)
    put("avatar_url", Ems.api + "/platform/user/avatars/" + Digests.md5Hex(apply.std.code))
    put("exchangeApply", apply)
    forward()
  }

  def download(): View = {
    val applyId = getLongId("exchangeApply")
    val apply = entityDao.get(classOf[ExchangeApply], applyId)
    val std = apply.std
    val bytes = DocHelper.toDoc(apply, entityDao)
    val contentType = MediaTypes.get("docx", MediaTypes.ApplicationOctetStream)
    Stream(new ByteArrayInputStream(bytes), contentType, std.code + "_" + std.name + "_" + apply.scheme.program.name + "_申请表.docx")
  }

}
