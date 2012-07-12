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
package net.metadata.qldarch.model

import net.liftweb.mapper._

class MimeType extends LongKeyedMapper[MimeType]
    with IdPK
    with OneToMany[Long,MimeType] {
  def getSingleton = MimeType

  object mimetype extends MappedString(this, 50)
  object resource extends MappedOneToMany(Resource, Resource.format)

  def forDisplay = mimetype.toString
}

object MimeType extends MimeType with LongKeyedMetaMapper[MimeType] with CRUDify[Long,MimeType] {
  override def dbTableName = "MimeType"
  
  def postInit = {
    List("audio/wav", "text/plain").map(mt =>
      if (MimeType.count(By(mimetype, mt)) == 0)
        MimeType.create.mimetype(mt).saveMe)
  }
}
