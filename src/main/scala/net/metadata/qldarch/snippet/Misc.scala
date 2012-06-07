package net.metadata.qldarch.snippet

import scala.xml.{NodeSeq, Group, Text}
import net.liftweb.common.{Box, Full, Empty}
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, RequestVar, FileParamHolder, SHtml}

class Misc {
  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)

  def upload(xhtml: Group): NodeSeq =
  if (S.get_?) bind("ulreq", chooseTemplate("mychoice", "get", xhtml),
                    "file_upload" -> SHtml.fileUpload(ul => theUpload(Full(ul))))
  else bind("ulfield", chooseTemplate("mychoice", "post", xhtml),
      "file_name" -> theUpload.is.map(v => Text(v.fileName)),
      "mime_type" -> theUpload.is.map(v => Box.legacyNullTest(v.mimeType).map(Text).openOr(Text("No mime type supplied"))), // Text(v.mimeType)),
      "length" -> theUpload.is.map(v => Text(v.file.length.toString)),
      "md5" -> theUpload.is.map(v => Text(hexEncode(md5(v.file))))
      );
}
