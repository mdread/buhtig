# buhtig

Is a scala client to the **Github** rest API. The main goal is to be damn easy, and to adapt to changes on the API. In fact it is just a utility to help you build requests against github in a fluid way.

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
val github = new Buhtig(token).client

// get information about this repository ;)
github.repos.mdread.buhtig.getSync

// same but with user and repo as parameters
github.repos("mdread", "buhtig").getSync

// get future
github.repos(user, repo).get

// get json content as a string (syncronously)
github.repos("mdread", "buhtig").getSync

// get an option containing the json response or None (syncronously)
github.repos("mdread", "buhtig").getOpt

// adding query parameters
github.search.repositories ? ("q" -> "buhtig", "language" -> "scala") getSync

// example of getting the content of a future with for-expressions
for(repo <- github.repos("mdread", "buhtig").get) {
    println(repo)
}
```

## How does it works?

Internally all http requests are managed by [Dispatch](http://github.com/dispatch/reboot). The external API is build around scala [Type Dynamic](http://docs.scala-lang.org/sips/completed/type-dynamic.html) functionality, which basically allows to intercept calls to missing methods, this is how you construct urls making method invocations.