package net.metadata.qldarch.model

import net.liftweb.common.Empty
import net.liftweb.mapper._

class Resource extends LongKeyedMapper[Resource]
    with IdPK {

  def getSingleton = Resource

  object creator extends MappedLongForeignKey(this, Person)
  object format extends MappedString(this, 50)
  object location extends MappedString(this, 100)
  object createdDate extends MappedString(this, 20)
  object fileName extends MappedString(this, 200)

  override def toXml = {
<resource>
  <format>{format}</format>
  <location>{location}</location>
  <createdDate>{createdDate}</createdDate>
  <fileName>{fileName}</fileName>
  <creator>
    <title>{creator.obj.map(_.title.toString).openOr("")}</title>
    <firstName>{creator.obj.map(_.firstName.toString).openOr("")}</firstName>
    <lastName>{creator.obj.map(_.lastName.toString).openOr("")}</lastName>
  </creator>
</resource>
  }
}

object Resource extends Resource
    with LongKeyedMetaMapper[Resource]
    with CRUDify[Long,Resource] {

  override def dbTableName = "resources"
  override def createMenuLoc = Empty
  override def deleteMenuLoc = Empty
  override def showAllMenuLoc = Empty
}
