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

class Resource extends LongKeyedMapper[Resource]
    with IdPK {

  def getSingleton = Resource


  object collection extends MappedLongForeignKey(this, CollectionResource) {
    override def displayName = "Collection"
    override def validSelectValues = Full(CollectionResource.findAll().map(c => (c.id.toLong, c.forDisplay)))
  }
  object creator extends MappedLongForeignKey(this, Person) {
    override def displayName = "Creator"
  }
  object title extends MappedString(this, 100) {
    override def displayName = "Title"
  }
  object format extends MappedLongForeignKey(this, MimeType) {
    override def displayName = "File Format"
  }
  object externalIdentifier extends MappedString(this, 100) {
    override def displayName = "External Identifier"
  }
  object description extends MappedText(this) {
    override def displayName = "Description"
  }
// FIXME: This should be a MappedManyToMany, and requires TopicConcept to work.
// Will also need to figure out how to make the UI work for this.
//  object subject extends MappedLongForeignKey(this, TopicConcept)
  object rights extends MappedString(this, 200) {
    override def displayName = "IP Rights"
  }
  object createdDate extends MappedDate(this) {
    override def displayName = "Created Date"
  }
  object fileName extends MappedString(this, 200) {
    override def displayName = "File Name"
  }
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
  override def showAllMenuLoc = Empty
  override def fieldsForEditing = List(title, format, externalIdentifier, description, rights)
}

