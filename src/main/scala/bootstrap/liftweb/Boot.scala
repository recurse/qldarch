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
package bootstrap.liftweb

import net.liftweb._
import net.liftweb.common.{Logger, Logback, Full}
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import net.liftweb.http.{LiftRules, NotFoundAsTemplate, ParsePath, OnDiskFileParamHolder}
import sitemap.{SiteMap, Menu, Loc}
import util.{NamedPF, Props}
import net.metadata.qldarch.model.{Resource, Person}

class Boot {
  def boot {
    val logUrl = LiftRules.getResource("/logback.xml")
    logUrl.foreach { x => Logger.setup = Full(Logback.withFile(x)) }

    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
                             Props.get("db.url") openOr "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
                             Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, Resource, Person)
  
    // Make sure we cache uploads to disk
    LiftRules.handleMimeFile = OnDiskFileParamHolder.apply

    // where to search snippet
    LiftRules.addToPackages("net.metadata.qldarch")

    // build sitemap
    val entries = (List(Menu("Home") / "index",
                        Menu("List Resources") / "resources" / "index",
                        Menu("Submit Resource") / "resources" / "create")
                        ) ::: Resource.menus ::: Person.menus
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
  }
}
