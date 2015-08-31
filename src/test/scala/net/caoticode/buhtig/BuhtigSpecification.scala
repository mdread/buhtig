package net.caoticode.buhtig

import org.specs2._
import com.ning.http.client.FluentStringsMap
import scala.io.Source

/**
 * @author Daniel Camarda <maniacal.dread@gmail.com>
 */

class BuhtigSpecification extends Specification { def is = s2"""

 This is the specification for Buhtig
 
 Buhtig should
   have the base url for github api set to api.github.com               $baseUri
   set the autorization header to the expected token                    $authToken
 
 GHBuilder should use dynamics to
   selectDynamic should compose uri fragments                           $sd1
   applyDynamic should compose uri fragments                            $ad1
   
 GHBuilder methods
   ? - should add query parameters to the request                       $ghb1
   
 Tests for known bugs
   issue #1 (https://github.com/mdread/buhtig/issues/1)                 $bug1
                                                                        """
   
   val token = loadToken()
   val buhtig = new Buhtig(token)
   val client = buhtig.syncClient

   def baseUri = buhtig.github.toRequest.getUrl must be_==("https://api.github.com")
   def authToken = buhtig.github.toRequest.getHeaders.getFirstValue("Authorization") must be_==(s"token $token")
   
   def sd1 = req(client.users.mdread).getUrl must be_==("https://api.github.com/users/mdread")
   def ad1 = req(client.users("mdread", "events")).getUrl must be_==("https://api.github.com/users/mdread/events")
   
   def ghb1 = {
     val fm = new FluentStringsMap()
     fm.add("q", "tetris")
     fm.add("sort", "star")
     
     req(client.search.repositories ? ("q" -> "tetris", "sort" -> "star")).getQueryParams() must be_==(fm)
   }
   
   def bug1 = req(client("users", "mdread")).getUrl must be_==("https://api.github.com/users/mdread")

   // utility methods
   
   def req(g: GHBuilder[_]) =  {
     val field = g.getClass.getDeclaredField("request")
     field.setAccessible(true)
     field.get(g).asInstanceOf[dispatch.Req].toRequest
   }
   
   def loadToken() = {
     Source.fromFile(getClass.getClassLoader.getResource("github.token").toURI(), "UTF-8").mkString
   }
   
}
