package net.caoticode.buhtig

import dispatch._
import dispatch.Defaults._

/**
 * @author Daniel Camarda <maniacal.dread@gmail.com>
 */

class Buhtig(token: String) {

  val authHeader = Map("Authorization" -> s"token $token")
  val github: Req = host("api.github.com").secure <:< authHeader

  def syncClient = new SyncClient(github)
  def asyncClient = new AsyncClient(github)

  def close() = Http.shutdown()
}
