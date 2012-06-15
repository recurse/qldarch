package net.metadata.qldarch.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http._
import net.metadata.qldarch.model.Resource

class UploadResource {
  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)
  private object theResource extends RequestVar[Box[Resource]](Empty)

  private var creator=""
  private var format=""
  private var location=""
  private var createdDate=""

  def doUpload () {
    val fileName = theUpload.is match {
      case Full(FileParamHolder(_, null, _, _)) => S.error("Empty resource file received")
      case Full(fileHolder @ FileParamHolder(_, mime, _, _)) => fileHolder match {
        case onDisk : OnDiskFileParamHolder => onDisk.localFile
        case _ => S.error("Internal Error (Unexpected type for fileHolder")
      }
      case _ => S.error("Error obtaining resource")
    }
    
    theResource(Full(Resource.create.creator(creator).format(format).
      location(location).createdDate(createdDate).fileName(fileName.toString()).saveMe))
  }

  def render(xhtml: NodeSeq): NodeSeq = 
    if (S.get_?)
      bind("uploadReq", chooseTemplate("request", "get", xhtml),
           "creator" -> SHtml.text(creator, creator = _),
           "format" -> SHtml.text(format, format = _),
           "location" -> SHtml.text(location, location = _),
           "createdDate" -> SHtml.text(createdDate, createdDate = _),
           "file_upload" -> SHtml.fileUpload(f => theUpload(Full(f))),
           "submit" -> SHtml.submit("Submit Resource", doUpload)
           );
    else
      bind("uploadReq", chooseTemplate("request", "post", xhtml),
           "file_name" -> theResource.is.map(r => Text(r.fileName)),
           "creator" -> theResource.is.map(r => Text(r.creator)),
           "format" -> theResource.is.map(r => Text(r.format))
      );
}
