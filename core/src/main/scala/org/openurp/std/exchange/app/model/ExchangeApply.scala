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
import org.beangle.data.model.pojo.Updated
import org.openurp.base.std.model.Student
import org.openurp.std.exchange.model.ExchangeProgram

import scala.collection.mutable

/** 交换生申请
 * */
class ExchangeApply extends LongId with Updated {

  var scheme: ExchangeScheme = _

  var program: ExchangeProgram = _

  var std: Student = _

  /** 志愿列表 */
  var choices: mutable.Buffer[ExchangeApplyChoice] = Collections.newBuffer[ExchangeApplyChoice]

  var mobile: String = _

  var address: String = _

  var email: String = _
  /** 平均绩点 */
  var gpa: Float = _

  /** 累计获得总学分 */
  var credits: Float = _

  /** 专业排名 */
  var rankInMajor: String = _

  /** 陈述 */
  var statements: String = _

  def choice1: String = {
    getChoice(1) match {
      case Some(c) => c.school.name + " " + c.major
      case None => ""
    }
  }

  def getChoice(idx: Int): Option[ExchangeApplyChoice] = {
    choices.find(_.idx == idx)
  }

  def choice2: String = {
    getChoice(2) match {
      case Some(c) => c.school.name + " " + c.major
      case None => ""
    }
  }
}
