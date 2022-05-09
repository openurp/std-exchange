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

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.beangle.commons.net.http.HttpUtils
import org.beangle.data.dao.EntityDao
import org.beangle.ems.app.{Ems, EmsApp}
import org.openurp.std.exchange.app.model.ExchangeApply
import org.openurp.std.info.model.{Home, SocialRelation}

import java.io.ByteArrayOutputStream
import java.net.URL

object DocHelper {

  def getApplyTemplate(apply: ExchangeApply): Option[URL] = {
    val url = new URL(s"${Ems.api}/platform/config/files/${EmsApp.name}/org/openurp/std/exchange/apply${apply.program.id}.docx")
    val status = HttpUtils.access(url)
    if (status.isOk) {
      Some(url)
    } else {
      None
    }
  }

  def toDoc(apply: ExchangeApply, entityDao: EntityDao): Array[Byte] = {
    val relations = entityDao.findBy(classOf[SocialRelation], "std", List(apply.std))

    val homes = entityDao.findBy(classOf[Home], "std", List(apply.std))
    val home = homes.headOption.getOrElse(new Home)

    val data = ApplyDataConvertor.convert(apply, home, relations.sortBy(_.id))
    val url = getApplyTemplate(apply).get
    DocHelper.toDoc(url, data)
  }

  private def toDoc(url: URL, data: collection.Map[String, String]): Array[Byte] = {
    val templateIs = url.openStream()
    val doc = new XWPFDocument(templateIs)
    import scala.jdk.javaapi.CollectionConverters._

    for (p <- asScala(doc.getParagraphs)) {
      val runs = p.getRuns
      if (runs != null) {
        for (r <- asScala(runs)) {
          var text = r.getText(0)
          if (text != null) {
            data.find { case (k, v) => text.contains("${" + k + "}") } foreach { e =>
              text = text.replace("${" + e._1 + "}", e._2)
              r.setText(text, 0)
            }
          }
        }
      }
    }

    for (tbl <- asScala(doc.getTables)) {
      for (row <- asScala(tbl.getRows)) {
        for (cell <- asScala(row.getTableCells)) {
          for (p <- asScala(cell.getParagraphs)) {
            for (r <- asScala(p.getRuns)) {
              var text = r.getText(0)
              if (text != null) {
                data.find { case (k, v) => text.contains("${" + k + "}") } foreach { e =>
                  text = text.replace("${" + e._1 + "}", e._2)
                  r.setText(text, 0)
                }
              }
            }
          }
        }
      }
    }
    val bos = new ByteArrayOutputStream()
    doc.write(bos)
    templateIs.close()
    bos.toByteArray
  }
}
