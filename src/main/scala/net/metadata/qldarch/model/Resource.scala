package net.metadata.qldarch.model

import net.liftweb.common.Empty
import net.liftweb.mapper._

class Resource extends LongKeyedMapper[Resource]
    with IdPK {

  def getSingleton = Resource

  object creator extends MappedString(this, 50)
  object format extends MappedString(this, 50)
  object location extends MappedString(this, 100)
  object createdDate extends MappedString(this, 20)
  object fileName extends MappedString(this, 50)
}

object Resource extends Resource
    with LongKeyedMetaMapper[Resource]
    with CRUDify[Long,Resource] {

  override def dbTableName = "resources"
  override def createMenuLoc = Empty
  override def deleteMenuLoc = Empty
  override def showAllMenuLoc = Empty
}
