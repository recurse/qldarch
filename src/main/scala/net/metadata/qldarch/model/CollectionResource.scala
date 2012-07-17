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

import net.liftweb.common.{Empty, Full}
import net.liftweb.mapper._
import net.liftweb.sitemap.{SiteMap, Menu, Loc}
import java.util.Date

class CollectionResource extends LongKeyedMapper[CollectionResource]
    with IdPK {

  def getSingleton = CollectionResource

  object creator extends MappedLongForeignKey(this, Person) {
    override def displayName = "Creator"
    override def dbNotNull_? = true
    override def validSelectValues = Full(Person.findAll().map(p => (p.id.toLong, p.forDisplay)))
  }
  object title extends MappedString(this, 100) {
    override def displayName = "Title"
    override def dbNotNull_? = true
  }
  object externalIdentifier extends MappedString(this, 100) {
    override def displayName = "External Identifier"
    override def dbNotNull_? = false
  }
  object description extends MappedText(this) {
    override def displayName = "Description"
    override def dbNotNull_? = false
  }
  object rights extends MappedString(this, 200) {
    override def displayName = "IP Rights"
    override def dbNotNull_? = true
  }
  object createdDate extends MappedDate(this) {
    override def displayName = "Created Date"
    override def dbNotNull_? = false
    override def defaultValue = new Date()
  }

  def forDisplay = title.toString

  override def toXml = {
<collection>
  <title>{title}</title>
  <externalIdentifier>{externalIdentifier}</externalIdentifier>
  <description>{description}</description>
  <rights>{rights}</rights>
  <createdDate>{createdDate}</createdDate>
  <creator>
    <title>{creator.obj.map(_.title.toString).openOr("")}</title>
    <givenName>{creator.obj.map(_.givenName.toString).openOr("")}</givenName>
    <familyName>{creator.obj.map(_.familyName.toString).openOr("")}</familyName>
  </creator>
</collection>
  }
}

object CollectionResource extends CollectionResource
    with LongKeyedMetaMapper[CollectionResource]
    with CRUDify[Long,CollectionResource] {

  override def dbTableName = "collections"
  override def fieldsForEditing = List(creator, title, externalIdentifier, description, rights)
}

