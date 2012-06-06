package net.metadata.qldarch.model

import net.liftweb.mapper._

class Resource extends LongKeyedMapper[Resource]
    with IdPK {

  def getSingleton = Resource

  object creator extends MappedString(this, 50)
  object format extends MappedString(this, 50)
  object location extends MappedString(this, 100)
  object createdDate extends MappedString(this, 15)
}

object Resource extends Resource
    with LongKeyedMetaMapper[Resource]
    with CRUDify[Long,Resource] {

  override def dbTableName = "resources"
}
