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

class TopicConcept extends LongKeyedMapper[TopicConcept]
    with IdPK {
//    with OneToMany[Long,TopicConcept] {
  def getSingleton = TopicConcept

  object label extends MappedString(this, 50) {
    override def displayName = "Label"
  }
//  object broader extends MappedLongForeignKey(this, TopicConcept)
//  object narrower extends MappedOneToMany(TopicConcept, TopicConcept.broader)

  def forDisplay = label //+ "(a " + broader + ")"
}

object TopicConcept extends TopicConcept with LongKeyedMetaMapper[TopicConcept] with CRUDify[Long,TopicConcept] {
  override def dbTableName = "TopicConcept"
  
  // Need to figure out how to create this top concept.
//  val top:TopicConcept = TopicConcept.create.label("TopConcept").broader(top).saveMe
}
