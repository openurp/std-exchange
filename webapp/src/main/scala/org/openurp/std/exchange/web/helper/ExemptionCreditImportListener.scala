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

import org.beangle.commons.lang.Numbers
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.doc.transfer.importer.{ImportListener, ImportResult}
import org.openurp.base.model.Project
import org.openurp.base.std.model.Student
import org.openurp.edu.exempt.model.ExternExemptCredit

import java.time.Instant

class ExemptionCreditImportListener(project: Project, entityDao: EntityDao) extends ImportListener {
  override def onStart(tr: ImportResult): Unit = {}

  override def onFinish(tr: ImportResult): Unit = {}

  override def onItemStart(tr: ImportResult): Unit = {
    var std: Student = null
    transfer.curData.get("stdCode") foreach { stdCode =>
      val builder = OqlBuilder.from(classOf[Student], "s")
      builder.where("s.project=:project and s.code=:stdCode", project, stdCode)
      val stds = entityDao.search(builder)
      if (stds.nonEmpty) {
        std = stds.head
      }
    }
    if (null == std) {
      tr.addFailure("错误的学号", transfer.curData.get("stdCode").orNull)
    } else {
      var maxValue: Float = -1
      transfer.curData.get("maxValue") foreach (d => maxValue = Numbers.toFloat(d.toString))
      if (maxValue < 0) {
        tr.addFailure("错误的学分上限", transfer.curData.get("maxValue").orNull)
        return
      }
      val query = OqlBuilder.from(classOf[ExternExemptCredit], "ec")
      query.where("ec.std=:std", std)
      val ecs = entityDao.search(query)
      val ec =
        if (ecs.nonEmpty) {
          ecs.head
        } else {
          val ec = new ExternExemptCredit
          ec.std = std
          ec
        }
      ec.maxValue = maxValue
      ec.updatedAt = Instant.now
      transfer.current = ec
    }
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    if (null != transfer.current) {
      val ec = transfer.current.asInstanceOf[ExternExemptCredit]
      entityDao.saveOrUpdate(ec)
    }
  }
}
