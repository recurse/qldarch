/*
 *  Copyright (C) 2012 e-Research Laboratory, School of ITEE,
 *                     The University of Queensland
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.metadata.qldarch.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http._
import net.metadata.qldarch.model.Person
import net.metadata.qldarch.model.Resource

class Resources extends Logger {
  def list =
    ".row *" #> Resource.findAll().map(r =>
      ".title *" #> Text(r.title) &
      ".recorder *" #> Text(r.creator.obj.map(c => c.forDisplay).openOr("Unknown recorder")) &
      ".collection *" #> Text(r.collection.obj.map(c => c.forDisplay).openOr("Unknown collection")) &
      ".format *" #> Text(r.format.obj.map(f => f.forDisplay).openOr("Unknown mimetype")) &
      ".date_recorded *" #> Text(r.createdDate.toString) &
      ".file_name *" #> Text(r.fileName.toString.reverse.takeWhile(_ != '/').reverse) &
      ".view" #> (".link [href]" #> ("/resources/view/" + r.id)) &
      ".edit" #> (".link [href]" #> ("/resources/edit/" + r.id)))
}
