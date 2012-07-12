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

import net.liftweb.common.Empty
import net.liftweb.mapper._

class Resource extends LongKeyedMapper[Resource]
    with IdPK {

  def getSingleton = Resource

  object creator extends MappedLongForeignKey(this, Person)
  object title extends MappedString(this, 100)
  object format extends MappedLongForeignKey(this, MimeType)
  object externalIdentifier extends MappedString(this, 100)
  object description extends MappedText(this)
// FIXME: This should be a MappedManyToMany, and requires TopicConcept to work.
// Will also need to figure out how to make the UI work for this.
//  object subject extends MappedLongForeignKey(this, TopicConcept)
  object rights extends MappedString(this, 200)
  object createdDate extends MappedDate(this)
  object fileName extends MappedString(this, 200)
// FIXME: Get this working as well. Requires user management.
//  object submitter extends MappedLongForeignKey(this, User)
//  object submittedDate extends MappedDate(this)

  override def toXml = {
<resource>
  <title>{title}</title>
  <format>{format.obj.map(_.mimetype.toString).openOr("")}</format>
  <externalIdentifier>{externalIdentifier}</externalIdentifier>
  <description>{description}</description>
  <rights>{rights}</rights>
  <createdDate>{createdDate}</createdDate>
  <fileName>{fileName}</fileName>
  <creator>
    <title>{creator.obj.map(_.title.toString).openOr("")}</title>
    <givenName>{creator.obj.map(_.givenName.toString).openOr("")}</givenName>
    <familyName>{creator.obj.map(_.familyName.toString).openOr("")}</familyName>
  </creator>
</resource>
  }
}

object Resource extends Resource
    with LongKeyedMetaMapper[Resource]
    with CRUDify[Long,Resource] {

  override def dbTableName = "resources"
  override def createMenuLoc = Empty
  override def editMenuLoc = Empty
  override def showAllMenuLoc = Empty
}
