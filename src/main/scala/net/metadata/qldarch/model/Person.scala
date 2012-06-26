package net.metadata.qldarch.model

import net.liftweb.mapper._

class Person extends LongKeyedMapper[Person]
    with IdPK
    with OneToMany[Long,Person] {
  def getSingleton = Person

  object firstName extends MappedString(this, 50)
  object lastName extends MappedString(this, 50)
  object title extends MappedString(this, 50)
  object resources extends MappedOneToMany(Resource, Resource.creator)

  def forDisplay = title + " " + firstName + " " + lastName
}

object Person extends Person with LongKeyedMetaMapper[Person] with CRUDify[Long,Person] {
  override def dbTableName = "people"
}
