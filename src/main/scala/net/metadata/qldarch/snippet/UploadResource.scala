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
package net.metadata.qldarch.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http._
import net.metadata.qldarch.model.MimeType
import net.metadata.qldarch.model.Person
import net.metadata.qldarch.model.Resource
import java.io.{File,FileInputStream,FileOutputStream,PrintWriter}
import net.liftweb.util.DefaultDateTimeConverter

class UploadResource extends Logger {
  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)
  private object theResource extends RequestVar[Box[Resource]](Empty)

  private var creator:Box[Person] = Empty
  private var format:Box[MimeType]= Empty
  private var title=""
  private var createdDate=""
  private var description=""

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
              createdDate(DefaultDateTimeConverter.parseDate(createdDate).openOr(null)).fileName(archFile.getPath).title(title).saveMe)
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
    val persons = Person.findAll().map(p => (p.id.toString, p.givenName + " " + p.familyName))
    val formats = MimeType.findAll().map(m => (m.id.toString, m.mimetype.toString))

    if (S.get_?)
      bind("request", chooseTemplate("choose", "get", xhtml),
           "creator" -> SHtml.select(persons, Empty, id => creator = Person.find(id.toLong)),
           "format" -> SHtml.select(formats, Empty, id => format = MimeType.find(id.toLong)),
           "title" -> SHtml.text(title, title = _),
           "createdDate" -> SHtml.text(createdDate, createdDate = _, "id" -> "createdDate"),
           "file_upload" -> SHtml.fileUpload(f => theUpload(Full(f))),
           "submit" -> SHtml.submit("Submit Resource", doUpload)
           );
    else
      bind("request", chooseTemplate("choose", "post", xhtml),
           "title" -> theResource.is.map(r => Text(r.title)),
           "file_name" -> theResource.is.map(r => Text(r.fileName)),
           "creator" -> theResource.is.map(r => Text(r.creator.obj.map(c => c.forDisplay).openOr("Unknown Creator"))),
         "format" -> theResource.is.map(r => Text(r.format.obj.map(m => m.forDisplay).openOr("Unrecognised MimeType")))
     )
  }
}
