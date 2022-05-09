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

package org.openurp.std.exchange.web.action.exemption

import org.beangle.commons.collection.Properties
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.excel.schema.ExcelSchema
import org.beangle.data.transfer.importer.ImportSetting
import org.beangle.web.action.annotation.response
import org.beangle.web.action.view.Stream
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.std.model.Student
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.exchange.model.ExemptionCredit
import org.openurp.std.exchange.web.helper.ExemptionCreditImportListener

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.time.format.DateTimeFormatter

class CreditAction extends RestfulAction[ExemptionCredit] with ProjectSupport {

  @response
  def loadStudent: Seq[Properties] = {
    val query = OqlBuilder.from(classOf[Student], "std")
    query.where("std.user.code=:code", get("q", ""))
    entityDao.search(query).map { std =>
      val p = new Properties()
      p.put("id", std.id)
      p.put("name", s"${std.state.get.department.name} ${std.user.name}")
      p
    }
  }

  @response
  def downloadTemplate(): Any = {
    val schema = new ExcelSchema()
    val sheet = schema.createScheet("数据模板")
    sheet.title("免修冲抵学分信息模板")
    sheet.remark("特别说明：\n1、不可改变本表格的行列结构以及批注，否则将会导入失败！\n2、必须按照规格说明的格式填写。\n3、可以多次导入，重复的信息会被新数据更新覆盖。\n4、保存的excel文件名称可以自定。")
    sheet.add("学号", "stdCode").length(20).required()
    sheet.add("免修学分合计上限", "maxValue").decimal(1, 100).required()
    val os = new ByteArrayOutputStream()
    schema.generate(os)
    Stream(new ByteArrayInputStream(os.toByteArray), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "免修冲抵学分.xlsx")
  }

  protected override def configImport(setting: ImportSetting): Unit = {
    setting.listeners = List(new ExemptionCreditImportListener(getProject, entityDao))
  }
}
