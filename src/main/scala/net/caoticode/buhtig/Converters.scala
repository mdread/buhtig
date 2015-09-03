package net.caoticode.buhtig

import com.ning.http.client.Response
import com.ning.http.client.FluentCaseInsensitiveStringsMap

import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods.{parse, compact, render}

/**
 * @author Daniel Camarda <maniacal.dread@gmail.com>
 */

object Converters {
  
  // converters for read operations
  
  type JSON = JValue
  type HEADER = FluentCaseInsensitiveStringsMap
  type HEADER_BODY = (HEADER, JSON)
  type HEADER_BODY_STRING = (HEADER, String)
  
  implicit val AsString = dispatch.as.String
  implicit val AsResponse = dispatch.as.Response
  implicit val AsByte = dispatch.as.Bytes
  
  implicit def AsJSON(r: Response): JSON = parse(AsString(r))
  implicit def AsHeader(r: Response): HEADER = r.getHeaders
  implicit def AsHeaderBody(r: Response): HEADER_BODY = (AsHeader(r), AsJSON(r))
  implicit def AsHeaderBodyString(r: Response): HEADER_BODY_STRING = (AsHeader(r), AsString(r))
  
  // converters for write operations
  
  implicit def FromString(v: String): String = v
  implicit def FromJson(v: JSON): String = compact(render(v))
  
}