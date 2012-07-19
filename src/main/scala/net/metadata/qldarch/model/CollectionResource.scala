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

import net.liftweb.common.{Empty, Failure, Full, Logger}
import net.liftweb.mapper._
import net.liftweb.sitemap.{SiteMap, Menu, Loc}
import net.liftweb.util.Props
import java.util.Date
import java.io.{File, PrintWriter}
import scala.xml.Text

class CollectionResource extends LongKeyedMapper[CollectionResource]
    with IdPK with Logger {

  def getSingleton = CollectionResource

  object creator extends MappedLongForeignKey(this, Person) {
    override def displayName = "Creator"
    override def dbNotNull_? = true
    override def validSelectValues = Full(Person.findAll().map(p => (p.id.toLong, p.forDisplay)))
    override def asHtml = Text(obj match {
        case Full(p) => p.forDisplay
        case _ => {
          error("Error obtaining Person for " + obj)
          "Unknown person"
        }
      });
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
  object filepath extends MappedString(this, 100) {
    override def displayName = "File Path"
    override def dbNotNull_? = true
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

  val dataPath = Props.get("net.metadata.qldarch.DataDirectory") match {
    case Full(x) => x
    case Failure(e, t, c) => throw new IllegalStateException("(" + c + ") DataDirectory could not be obtained: " + e, t openOr null)
    case Empty => throw new IllegalStateException("DataDirectory property missing")
  }

  val dataDir = new File(dataPath)
  if (!dataDir.exists) {
    error("Could not find: " + dataDir.getAbsoluteFile);
    throw new IllegalArgumentException("Data Directory does not exist: " + dataDir);
  }

  override def dbTableName = "collections"
  override def fieldsForEditing = List(creator, title, externalIdentifier, description, rights)
  override def beforeSave = List( c => {
    val filepath = """\s""".r.replaceAllIn(c.title.toString, "") + "." + c.createdDate.getTime.toString
    val cdir = new File(dataDir, filepath)
    c.filepath(cdir.getAbsoluteFile.toString)
    if (cdir.exists()) {
      throw new IllegalStateException("Collection path already exists")
    } else {
      if (!cdir.mkdir()) {
        throw new IllegalStateException("Could not create collection directory");
      }

      val metaFile = new File(cdir, filepath + ".xml")
      val ob = new PrintWriter(metaFile)
      ob.write(c.toXml.toString)
      ob.close()
    }
  });
}

