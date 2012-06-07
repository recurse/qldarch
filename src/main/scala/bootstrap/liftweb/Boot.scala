package bootstrap.liftweb

import net.liftweb._
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import net.liftweb.http.{LiftRules, NotFoundAsTemplate, ParsePath, OnDiskFileParamHolder}
import sitemap.{SiteMap, Menu, Loc}
import util.{NamedPF, Props}
import net.metadata.qldarch.model.Resource



class Boot {
  def boot {
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
    Schemifier.schemify(true, Schemifier.infoF _, Resource)
  
    // Make sure we cache uploads to disk
    LiftRules.handleMimeFile = OnDiskFileParamHolder.apply

    // where to search snippet
    LiftRules.addToPackages("net.metadata.qldarch")

    // build sitemap
    val entries = (List(Menu("Home") / "index",
                        Menu("Submit Resource") / "submit" )) ::: Resource.menus
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
  }
}
