# buhtig

Scala client for **Github** rest API. The main goal is to be damn easy, and to adapt to changes on the API. In fact it is just a utility to help you build requests against github in a fluid way.

## why?

## Installation

### sbt
### sources

    :> git clone ...
    :> cd buhtig
    :> sbt package

the generated jar file can be found under target/scala_{version}

## Usage

```scala
val buhtig = new Buhtig(token)
val github = buhtig.client

// get information about this repository ;)
github.repos.mdread.buhtig.sync

// same but with user and repo as parameters
github.repos("mdread", "buhtig").sync

// get future
github.repos(user, repo).async

// get json content as a string (syncronously)
github.repos("mdread", "buhtig").sync

// get an option containing the json response or None (syncronously)
github.repos("mdread", "buhtig").syncOpt

// adding query parameters
github.search.repositories ? ("q" -> "buhtig", "language" -> "scala") sync

// example of getting the content of a future with for-expressions
for(repo <- github.repos("mdread", "buhtig").async) {
    println(repo)
}

// close the client releasing threads and connections
buhtig.close
```

## How does it works?

Internally all http requests are managed by [Dispatch](http://github.com/dispatch/reboot). The external API is build around scala [Type Dynamic](http://docs.scala-lang.org/sips/completed/type-dynamic.html) functionality, which basically allows to intercept calls to missing methods, this is how you construct urls making method invocations.