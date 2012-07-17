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

class Person extends LongKeyedMapper[Person]
    with IdPK
    with OneToMany[Long,Person] {
  def getSingleton = Person

  object givenName extends MappedString(this, 50) {
    override def displayName = "Given Name"
    override def dbNotNull_? = true
  }
  object familyName extends MappedString(this, 50) {
    override def displayName = "Family Name"
    override def dbNotNull_? = true
  }
  object title extends MappedString(this, 50) {
    override def displayName = "Title"
    override def dbNotNull_? = false
  }
  object resources extends MappedOneToMany(Resource, Resource.creator)

  def forDisplay = title + " " + givenName + " " + familyName
}

object Person extends Person with LongKeyedMetaMapper[Person] with CRUDify[Long,Person] {
  override def dbTableName = "people"
}
