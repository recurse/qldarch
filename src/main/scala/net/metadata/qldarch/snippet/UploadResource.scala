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
import net.metadata.qldarch.model.CollectionResource
import java.io.{File,FileInputStream,FileOutputStream,PrintWriter}
import java.util.Date
import net.liftweb.util.DefaultDateTimeConverter

class UploadResource extends Logger {
  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)
  private object theResource extends RequestVar[Box[Resource]](Empty)

  private var creator:Box[Person] = Empty
  private var collection:Box[CollectionResource] = Empty
  private var format:Box[MimeType]= Empty
  private var title=""
  private var createdDate=""
  private var description=""

  // Replace this with Fedora Commons or some other repository
  def doUpload () {
    warn("doUpload called")
    val (_, tempFile:File) = theUpload.is match {
      case Full(FileParamHolder(_, null, _, _)) => S.error("Empty resource file received")
      case Full(fileHolder @ FileParamHolder(_, mime, _, _)) => fileHolder match {
        case onDisk : OnDiskFileParamHolder => (onDisk.fileName, onDisk.localFile)
        case _ => S.error("Internal Error (Unexpected type for fileHolder")
      }
      case _ => S.error("Error obtaining resource")
    }

    val dataDir = new File(collection.open_!.filepath.toString)
    warn("Using " + dataDir);

    if (!dataDir.exists) {
      error("Data directory does not exist")
      S.error("Data directory does not exist")
    } else if (!dataDir.isDirectory) {
      error("Data directory is not a directory")
      S.error("Data directory is not a directory")
    } else {
      val uploadDir = new File(dataDir, tempFile.getName)
      if (!uploadDir.mkdir()) {
        error("Could not create upload directory")
        S.error("Could not create upload directory")
      } else {
        val fileDate = DefaultDateTimeConverter.parseDate(createdDate).openOr(new Date())
        val filename = """\s""".r.replaceAllIn(title.toString, "") + "." + fileDate.getTime.toString
        val archFile = new File(uploadDir, filename)
        warn("Copying " + tempFile + " to " + archFile)
        val ic = new FileInputStream(tempFile).getChannel()
        val oc = new FileOutputStream(archFile).getChannel()
        ic.transferTo(0, ic.size, oc)
        ic.close
        oc.close

        theResource(creator match {
          case Full(c) => Full(Resource.create.collection(collection).creator(creator).format(format).
              createdDate(fileDate).fileName(archFile.getPath).title(title).saveMe)
          case _ => Empty
        })
        theResource.is.foreach(r => {
          val metaFile = new File(uploadDir, filename + ".xml")
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
    val collections = CollectionResource.findAll().map(c => (c.id.toString, c.forDisplay))

    if (S.get_?)
      bind("request", chooseTemplate("choose", "get", xhtml),
           "creator" -> SHtml.select(persons, Empty, id => creator = Person.find(id.toLong)),
           "collection" -> SHtml.select(collections, Empty, id => collection = CollectionResource.find(id.toLong)),
           "format" -> SHtml.select(formats, Empty, id => format = MimeType.find(id.toLong)),
           "title" -> SHtml.text(title, title = _),
           "createdDate" -> SHtml.text(createdDate, createdDate = _, "id" -> "createdDate"),
           "file_upload" -> SHtml.fileUpload(f => theUpload(Full(f))),
           "submit" -> SHtml.submit("Submit Resource", doUpload)
           );
    else
      bind("request", chooseTemplate("choose", "post", xhtml),
           "title" -> theResource.is.map(r => Text(r.title)),
           "collection" -> theResource.is.map(r => Text(r.collection.obj.map(c => c.forDisplay).openOr("Unknown Collection"))),
           "file_name" -> theResource.is.map(r => Text(r.fileName)),
           "creator" -> theResource.is.map(r => Text(r.creator.obj.map(c => c.forDisplay).openOr("Unknown Creator"))),
         "format" -> theResource.is.map(r => Text(r.format.obj.map(m => m.forDisplay).openOr("Unrecognised MimeType")))
     )
  }
}
