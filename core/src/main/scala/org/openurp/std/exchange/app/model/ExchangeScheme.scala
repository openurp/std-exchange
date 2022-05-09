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

package org.openurp.std.exchange.app.model

import org.beangle.commons.collection.Collections
import org.beangle.data.model.LongId
import org.beangle.data.model.pojo.{InstantRange, Named}
import org.openurp.base.model.Project
import org.openurp.base.model.ExternSchool
import org.openurp.std.exchange.model.ExchangeProgram

import java.time.Instant
import scala.collection.mutable

class ExchangeScheme extends LongId with InstantRange with Named {

  var project: Project = _

  var program: ExchangeProgram = _

  var grades: String = _

  var minGpa: Float = _

  var choiceCount: Int = _

  var schools: mutable.Buffer[ExternSchool] = Collections.newBuffer[ExternSchool]

  def opened: Boolean = {
    this.within(Instant.now)
  }
}
