package net.caoticode

import dispatch._, Defaults._

class Buhtig(token: String) {

  val authHeader = Map("Authorization" -> s"token $token")
  val github: Req = host("api.github.com").secure <:< authHeader

  def client = new GHBuilder(github)

  def close() = Http.shutdown()
}

class GHBuilder(request: Req) extends Dynamic {
  import scala.language.dynamics

  def selectDynamic(name: String) = new GHBuilder(request / name)

  def applyDynamic(name: String)(args: Any*) = {
    val req = args.foldLeft(request / name) { case (z, e) => z / e.toString }

    new GHBuilder(req)
  }

  def ?(params: (String, String)*) = {
    val req = params.foldLeft(request) { case (z, e) => z.addQueryParameter(e._1, e._2) }

    new GHBuilder(req)
  }

  def async = Http(request OK as.String)
  
  def sync = {
    val res = Http(request OK as.String)
    res()
  }
  
  def syncOpt = {
    val res = Http(request OK as.String).option
    res()
  }

  // def fullUrl(urlStr: String) = new GHBuilder(url(urlStr) <:< authHeader)
}