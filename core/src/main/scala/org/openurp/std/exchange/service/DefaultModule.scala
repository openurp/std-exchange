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

package org.openurp.std.exchange.service

import org.beangle.cdi.bind.BindModule
import org.openurp.base.service.impl.SemesterServiceImpl
import org.openurp.edu.grade.service.impl.GradeRateServiceImpl
import org.openurp.edu.program.domain.{DefaultCoursePlanProvider, DefaultProgramProvider}
import org.openurp.std.exchange.service.impl.ExchangeServiceImpl

class DefaultModule extends BindModule {
  override protected def binding(): Unit = {
    bind(classOf[DefaultProgramProvider])
    bind(classOf[DefaultCoursePlanProvider])
    bind(classOf[SemesterServiceImpl])
    bind(classOf[ExchangeServiceImpl])
    bind(classOf[GradeRateServiceImpl])
  }
}
