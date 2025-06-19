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

package org.openurp.std.exchange.web.action

import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.ExternSchool
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.exchange.model.ExchangeProgram

class ProgramAction extends RestfulAction[ExchangeProgram] with ProjectSupport {

  override def editSetting(entity: ExchangeProgram): Unit = {
    put("schools", entityDao.getAll(classOf[ExternSchool]))
    super.editSetting(entity)
  }

  override def saveAndRedirect(entity: ExchangeProgram): View = {
    entity.schools.clear()
    val schoolIds = getAll("schoolId", classOf[Int])
    entity.schools ++= entityDao.find(classOf[ExternSchool], schoolIds).toBuffer

    entity.project= getProject
    super.saveAndRedirect(entity)
  }

}
