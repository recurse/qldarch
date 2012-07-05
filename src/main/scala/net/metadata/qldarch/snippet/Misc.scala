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
