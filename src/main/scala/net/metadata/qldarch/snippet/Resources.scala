package net.metadata.qldarch.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http._
import net.metadata.qldarch.model.Person
import net.metadata.qldarch.model.Resource

class Resources extends Logger {
  def list =
    ".row *" #> Resource.findAll().map(r =>
      ".recorder *" #> Text(r.creator.obj.map(c => c.forDisplay).openOr("Unknown recorder")) &
        ".format *" #> Text(r.format) &
        ".location *" #> Text(r.location) &
        ".date_recorded *" #> Text(r.createdDate.toString) &
        ".file_name *" #> Text(r.fileName) &
        ".view" #> (".link [href]" #> ("/resources/view/" + r.id)) &
        ".edit" #> (".link [href]" #> ("/resources/edit/" + r.id)))
}
