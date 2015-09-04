# buhtig

Scala client for **Github** rest API. The main goal is to be damn easy, and to adapt to changes on the API. In fact it is just a utility to help you build requests against github in a fluid way.

## why?

The idea is to have a basic utility to interact with [Github REST API](https://developer.github.com/), that does not require you to study a new API. If you already know github REST API you already know how to use this library, and because it is no more than a utility to help you build requests, if the API changes you don't need to wait for a new version to be released.

## Installation

### sbt

```
libraryDependencies += "net.caoticode.buhtig" %% "buhtig" % "0.3.1"
```

The library is cross compiled for scala 2.10 / 2.11

### sources

```bash
:> git clone https://github.com/mdread/buhtig.git
:> cd buhtig
:> sbt package
```

The generated jar file can be found under target/scala_{version} (don't forget to add [Dispatch](http://github.com/dispatch/reboot) and [json4s](https://github.com/json4s/json4s) as dependency).

To execute tests create a file named *github.token* under *src/test/resources* with a valid github api token.

## Usage

### Getting a client

To start using the library we need an instance of the *client*, and for that we first need to instantiate *Buhtig* class with a valid github token.

```scala
val token = "..." // your personal API token
val buhtig = new Buhtig(token)
```

Now we can choose to get a synchronous or an asynchronous client, with the synchronous one the execution of the program is going to stop on every request waiting for the response from github servers, while with the asynchronous one the execution is not going to stop and the requests are going to be processed in background by [Scala Futures](http://docs.scala-lang.org/overviews/core/futures.html).

To get the synchronous client do:

```scala
val client = buhtig.syncClient
```

For the asynchronous one:

```scala
val client = buhtig.asyncClient
```

The other method you are going to use from `Buhtig` class is `close`, which closes the underlying **Dispatch** client. For this just call `buhtig.close()` when all your operations are done.

### Constructing URLs

The *client* we get before is actually an immutable class which holds a fragment of the URL we want to compose, and has some handy methods to build it and execute the request.

#### Concatenating URL fragments

The easiest way is to use method chaining, where every method call make a fragment of the URL we want to compose, let's say we want to get information about this repository, from github API we need to make a GET request to the URL https://api.github.com/repos/mdread/buhtig, which we can do as follow:

```scala
val repo = client.repos.mdread.buhtig.get[JSON]
```

Leaving out for the moment the last `get[JSON]` call, the URL https://api.github.com/repos/mdread/buhtig gets constructed with `client.repos.mdread.buhtig`, actually any URL can be constructed this way. Also you can choose to build part of the URL as parameter of a method call, as in the following example:

```scala
val gistID = "7655832"
val gist = client.gists(gistID).get[JSON]
```

 Here the URL that gets constructed is https://api.github.com/gists/7655832, where *7655832* is the ID of a Gist. Can be useful to build requests this way when a part of the URL can come from an external source and can not be hard-coded.

Another way of composing URL fragments is to use the `/` method of the client, this way the first example can be rewritten as:

```scala
val repo = (client / "repos" / "mdread" / "buhtig").get[JSON]
```

#### Query parameters

Query parameters can be added with the `?` method of the client, for example to build the URL https://api.github.com/search/repositories?q=tetris&sort=stars to search for tetris repos we can do as follows:

```scala
val tetris = (client.search.repositories ? ("q" -> "tetris", "sort" -> "stars")).get[JSON]
```

#### Write operations

So far we have only made GET calls to Github API, but some parts of it requires to use POST, PUT, PATCH or DELETE. To do so just call the appropriate method after constructing the url, and pass the body of the request as parameter, for example for creating a new Gist:

```scala
val request = client.gists POST """
  {
    "description": "the description for this gist",
    "public": true,
    "files": {
      "file1.txt": {
        "content": "String file contents"
      }
    }
  }
  """

println(request.get[String])
```

The body of the request can also be a JSON built with json4s, so the last example can be rewritten as follows:

```scala
val request = client.gists POST
  ("description" -> "the description for this gist") ~
  ("public" -> true) ~
  ("files" ->
    ("file1.txt" ->
      ("content" -> "String file contents")))

println(request.get[String])
```

Actually if we check how the function POST is defined

```scala
def POST[S](value: S)(implicit converter: S => String) = { ... }
```

we see it can take any parameter provided there is a function in scope which can convert it to a String, so if you use a different library for JSON parsing, or some custom object holding your data, you can always implement an **S => String** function and declare it implicit in scope to use the custom object directly.

For the DELETE method there are no parameters, so for example to delete a gist just do:

```scala
(client / "gists" / "113533257213d2883e74").DELETE.get[String]
```

### Getting the result

In all previous examples we have used the method `get[T]` at the end of the constructed URL, this method is the one which effectively executes the request, there is also a `getOpt[T]` method which returns an [Option[T]](http://www.scala-lang.org/api/current/index.html#scala.Option). Depending on the client you are using (synchronous or asynchronous) those methods will return directly the expected result, or a Future which will eventually return the expected value when is ready.

Looking at the definition of `get` method:

```scala
def get[T](implicit x: (Response => T)): T  = { ... }
```

we see it also depends on a function which converts a Response object (com.ning.http.client.Response) to the expected value, there are predefined converters for String, JSON (type alias for JValue), Response, Byte and also for getting the HEADER (type alias for com.ning.http.client.FluentCaseInsensitiveStringsMap) and a tuple of HEADER_BODY (tuple of type (HEADER, JSON) ). To convert to any custom type implement a **Response => T** function and declare it implicit in scope.

### Examples

Here some complete examples with all needed imports and declarations

#### Creating a new Gist

Creating a new Gist and printing the id

```scala
import net.caoticode.buhtig.Buhtig
import net.caoticode.buhtig.Converters._
import org.json4s.native.JsonMethods._

val token = "..." // your personal API token
val buhtig = new Buhtig(token)
val client = buhtig.syncClient

val request = client.gists POST """
 {
   "description": "the description for this gist",
   "public": true,
   "files": {
     "file1.txt": {
       "content": "String file contents"
     }
   }
 }
 """

val gist = request.get[JSON]

println(compact(render( (gist \ "id") )))

client.close()
```

#### Asynchronously listing all contributors of all repos of a user

In this example we are going to use the asynchronous client to list all contributors of all repositories of the user *apache*

```scala
import net.caoticode.buhtig.Buhtig
import net.caoticode.buhtig.Converters._
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val token = "..." // your personal API token
val buhtig = new Buhtig(token)
val client = buhtig.asyncClient

def listRepos(user: String) = client.users(user).repos.get[JSON]
def listContributors(user: String, repo: String) = client.repos(user, repo).stats.contributors.get[JSON]

// wait for successful resolution of the Future
for(repo <- listRepos("apache")) {

  // extract repository names with json4s api
  val names = for {
    JObject(obj) <- repo
    JField("name", JString(name)) <- obj
  } yield name

  // convert the List of futures to a future of List
  val contrsList = Future.sequence(names map {e => listContributors("apache", e)})

  // wait for completion of all futures
  contrsList.onSuccess {
    case contribtors => {

      // extract the list of contributors from the list of JSON responses
      val logins = contribtors.flatMap(cont => cont filterField {
       case JField("login", _) => true
       case _ => false
      }).map(e => compact(render(e._2))).mkString(", ")

      println(logins)

      buhtig.close()
    }
  }
}

```

## How does it works?

Internally all HTTP requests are managed by [Dispatch](http://github.com/dispatch/reboot). The external API is build around Scala [Type Dynamic](http://docs.scala-lang.org/sips/completed/type-dynamic.html) functionality, which basically allows to intercept calls to missing methods, this is how you construct URLs making method invocations. Also it uses [json4s](https://github.com/json4s/json4s) to parse JSON.
