/*
 * Copyright (C) 2005, The OpenURP Software.
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

import java.time.format.DateTimeFormatter
import org.beangle.data.transfer.exporter.DefaultPropertyExtractor
import org.openurp.std.exchange.model.ExchangeGrade

class ExchangeGradePropertyExtractor extends DefaultPropertyExtractor {
  override def getPropertyValue(target: Object, property: String): Any = {
    val eg = target.asInstanceOf[ExchangeGrade]
    property match {
      case "courseCode" => "01"
      case "creditHours" => "0"
      case "acquiredOn" => DateTimeFormatter.ofPattern("yyyyMM").format(eg.acquiredOn)
      case "courseCodes" => eg.courses.map(c => s"${c.code}").mkString("\r\n")
      case "courseNames" => eg.courses.map(c => s"${c.name}").mkString("\r\n")
      case "courseCredits" => eg.courses.map(c => s"${c.credits}").mkString("\r\n")
      case "courses" =>
        if (eg.courses.isEmpty) {
          "--"
        } else {
          eg.courses.map(c => s"${c.name} ${c.credits}分").mkString("\r\n")
        }
      case _ =>
        super.getPropertyValue(target, property)
    }
  }
}
