package net.caoticode.buhtig

import dispatch._
import dispatch.Defaults._
import com.ning.http.client.Response

/**
 * @author Daniel Camarda <maniacal.dread@gmail.com>
 */

trait GHBuilder[T <: GHBuilder[T]] extends Dynamic {
  import scala.language.dynamics

  val request: Req
  def construct(request: Req): T
  
  def selectDynamic(name: String) = construct(request / name)

  def applyDynamic(name: String)(args: Any*) = {
    val seed = if(name == "apply") request else request / name
    
    val req = args.foldLeft(seed) { case (z, e) => z / e.toString }

    construct(req)
  }

  def ?(params: (String, String)*) = {
    val req = params.foldLeft(request) { case (z, e) => z.addQueryParameter(e._1, e._2) }

    construct(req)
  }

  // def fullUrl(urlStr: String) = new GHBuilder(url(urlStr) <:< authHeader)
}

class SyncClient(val request: Req) extends GHBuilder[SyncClient] {
  
  override def construct(request: Req): SyncClient = new SyncClient(request)

  def get[T](implicit x: (Response => T)): T = getOpt(x).get
  
  def getOpt[T](implicit x: (Response => T)): Option[T] = {
    val res = Http(request OK x).option
    res()
  }
  
}

class AsyncClient(val request: Req) extends GHBuilder[AsyncClient] {
  
  override def construct(request: Req): AsyncClient = new AsyncClient(request)

  def get[T](implicit x: (Response => T)): Future[T] = Http(request OK x)
  
  def getOpt[T](implicit x: (Response => T)): Future[Option[T]] =  Http(request OK x).option
}
