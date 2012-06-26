package net.metadata.qldarch.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http._
import net.metadata.qldarch.model.Person
import net.metadata.qldarch.model.Resource
import java.io.{File,FileInputStream,FileOutputStream,PrintWriter}

class UploadResource extends Logger {
  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)
  private object theResource extends RequestVar[Box[Resource]](Empty)

  private var creator:Box[Person] = Empty;
  private var format=""
  private var location=""
  private var createdDate=""

  // Replace this with Fedora Commons or some other repository
  private val dataDir = new File("data/")

  def doUpload () {
    warn("doUpload called")
    val (clientName:String, tempFile:File) = theUpload.is match {
      case Full(FileParamHolder(_, null, _, _)) => S.error("Empty resource file received")
      case Full(fileHolder @ FileParamHolder(_, mime, _, _)) => fileHolder match {
        case onDisk : OnDiskFileParamHolder => (onDisk.fileName, onDisk.localFile)
        case _ => S.error("Internal Error (Unexpected type for fileHolder")
      }
      case _ => S.error("Error obtaining resource")
    }

    if (!dataDir.exists && !dataDir.mkdir()) {
      S.error("Could not make data directory")
    } else if (!dataDir.isDirectory) {
      S.error("Data directory is not a directory")
    } else {
      val uploadDir = new File(dataDir, tempFile.getName)
      if (!uploadDir.mkdir()) {
        S.error("Could not create upload directory")
      } else {
        val archFile = new File(uploadDir, clientName)
        val ic = new FileInputStream(tempFile).getChannel()
        val oc = new FileOutputStream(archFile).getChannel()
        ic.transferTo(0, ic.size, oc)
        ic.close
        oc.close

        theResource(creator match {
          case Full(c) => Full(Resource.create.creator(creator).format(format).
              location(location).createdDate(createdDate).fileName(archFile.getPath).saveMe)
          case _ => Empty
        })
        theResource.is.foreach(r => {
          val metaFile = new File(uploadDir, clientName + ".xml")
          val ob = new PrintWriter(metaFile)
          ob.write(r.toXml.toString)
          ob.close()
        })
      }
    }
  }

  def render(xhtml: NodeSeq): NodeSeq = {
    val persons = Person.findAll().map(p => (p.id.toString, p.firstName + " " + p.lastName))

    if (S.get_?)
      bind("request", chooseTemplate("choose", "get", xhtml),
           "creator" -> SHtml.select(persons, Empty, id => creator = Person.find(id.toLong)),
           "format" -> SHtml.text(format, format = _),
           "location" -> SHtml.text(location, location = _),
           "createdDate" -> SHtml.text(createdDate, createdDate = _, "id" -> "createdDate"),
           "file_upload" -> SHtml.fileUpload(f => theUpload(Full(f))),
           "submit" -> SHtml.submit("Submit Resource", doUpload)
           );
    else
      bind("request", chooseTemplate("choose", "post", xhtml),
           "file_name" -> theResource.is.map(r => Text(r.fileName)),
           "creator" -> theResource.is.map(r => Text(r.creator.obj.map(c => c.forDisplay).openOr("Unknown Creator"))),
           "format" -> theResource.is.map(r => Text(r.format))
      );
  }
}
