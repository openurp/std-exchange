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

package org.openurp.std.exchange.service.impl

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.OqlBuilder
import org.openurp.base.edu.model.Course
import org.openurp.base.std.model.Student
import org.openurp.edu.exempt.model.ExchExemptCredit
import org.openurp.edu.exempt.service.impl.ExemptionServiceImpl
import org.openurp.edu.extern.model.ExternGrade
import org.openurp.std.exchange.service.ExchangeService

import java.time.Instant

class ExchangeServiceImpl extends ExemptionServiceImpl, ExchangeService {

  override def recalcExemption(std: Student): Unit = {
    //重新统计已经免修的学分
    val ecBuilder = OqlBuilder.from(classOf[ExchExemptCredit], "ec")
    ecBuilder.where("ec.std=:std", std)
    val ec = entityDao.search(ecBuilder).headOption match {
      case Some(e) => e
      case None =>
        val e = new ExchExemptCredit
        e.std = std
        e
    }
    val exemptedCourses = Collections.newSet[Course]
    entityDao.findBy(classOf[ExternGrade], "externStudent.std", List(std)) foreach { eg =>
      exemptedCourses ++= eg.exempts
    }
    ec.exempted = exemptedCourses.toList.map(_.defaultCredits).sum
    ec.updatedAt = Instant.now
    entityDao.saveOrUpdate(ec)
  }

}
