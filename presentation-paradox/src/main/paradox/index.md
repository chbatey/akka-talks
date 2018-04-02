@@@section { data-background="#15a9ce" }

### Responsive, back-pressured services with Akka 

##### Christopher Batey 
##### @chbatey

@@@@notes

* Me
* Lightbend: Scala, Akka, Play
* Responsive systems using async, resource efficient
* Scalable systems that behave well under load

@@@@

@@@

@@@section

## Akka toolkit

Actor model

@span[Clustering]{ .fragment }

@span[Persistence (Event Sourcing)]{ .fragment }

@span[HTTP]{ .fragment }

@span[Streams]{ .fragment }

@@@@notes

* Core; actor based concurrency
* Distribution and location transparency
* HTTP and streams, reactive streams

@@@@


@@@


@@@section

## Responsive

* For a single request
* Remain responsive under load

@@@@notes

* Hanging, crashing vs being slow
* Capacity planned to 1000 concurrent requests
* Sized your hear to 2GB

@@@@


@@@


@@@section

## Scalable

* Single process scalability or resource efficiency
* Multi node scalability

@@@@notes

- Scalable for those who need it. E.g. 5TPS on a huge box
- Scalable in process: Execution model appropriate
- Resource efficiency
- Scalable across machines


@@@@

@@@

@@@section

## Asynchronous

* Programming model
    * CompletableFuture from the JDK
    * Scala Futures
    * Actors and Streams from Akka
    * Observables and Flowables from RxJava
* Network 
    * Does a request over the network use a Thread?


@@@@notes

* How many threads do you need to service a 1000 concurrent requests?
* Async programming model vs actually async all the way down

@@@@

@@@

@@@section

## Back pressure

* How do we stop a fast producer overloading a slow consumer?
* How is demand signalled?
* How do we prevent bring data from a database at a faster rate than the client can consumer?

@@@@notes

* Dealing with components that run at different speeds
* Much more important when moving away from thread per request

@@@@

@@@


@@@section

## Presentation take aways

* What does async give us?
* What does flow control give us?
* What is the reactive streams specification?
* Flow control with Akka streams
* Http Client -> TCP -> Http Server -> TCP -> Apache Cassandra (slow client)

@@@

@@@section

## Use case

* HTTP Service, endpoints for:
    * User information from database
    * Getting user activity over a large time span

@@@@notes

* Small request vs large request

@@@@

@@@

@@@section

## Requirements 

* Respond in a timely manner, even if it is a failure
* Don't do any unnecessary work
* Constant memory footprint

@@@@notes

* Resource efficiency/scalability 

@@@@

@@@

@@@section

<img src="response-time.png" style="width: 1000px;"/>

@@@@notes

* Control over when your application responds
    * Service time vs response time
    * Queues and buffers
* Dependencies
    * Don't make their problem your problem


@@@@

@@@

@@@section

## Execution Models 

@@@@notes

* Thread per request, hystrix
* Future based programming
* Akka stream based programming

@@@@


@@@

@@@section

### Traditional synchronous model

@@snip[Synchronous.java]($root$/../http-streams/src/main/java/rs/async/Synchronous.java){#service}

@@snip[Synchronous.java]($root$/../http-streams/src/main/java/rs/async/Synchronous.java){#perform}

@@@@notes

* Network vs compute

@@@@

@@@

@@@section


@@snip[SynWebService.scala]($root$/../sync-examples/src/main/scala/info/batey/sync/SyncWebFramework.scala){#sync-post group="scala"}

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/SyncWebFramework.java){#sync-post group="java"}

@@@@notes

 
* JAX-RS, Spring
* Probably got some annotations for serialisation
* Inherently not scalable

@@@@

@@@

@@@section

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/scala/info/batey/sync/SyncWebFramework.scala){#sync-post-times group="scala"}

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/SyncWebFramework.java){#sync-post-times group="java"}

@@@@notes

* TCP connection/receive timeouts

@@@@

@@@

@@@section

### Asynchronous actor approach

@@snip[x]($root$/../http-streams/src/main/java/rs/async/Asynchronous.java){#actor group="java"}

@@snip[x]($root$/../http-streams/src/main/java/rs/async/Asynchronous.java){#enqueue group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/rs/async/Asynchronous.scala){#actor group="scala"}

@@snip[x]($root$/../http-streams/src/main/scala/rs/async/Asynchronous.scala){#enqueue group="scala"}

@@@@notes

* Task in mailbox. Saves threads.
* Resilience. Back a slide, highlight the sending thread no longer has to deal with the exception
* Just one way to make this async

@@@@

@@@


@@@section

## Asynchronous

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/AsyncWebFramework.scala){#async-request group="scala"}

@@snip[x]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/AsyncWebFramework.java){#async-request group="java"}

* Timeout independent to dependency times
* Option not to block on IO
* Works well for small responses that are ready to go


@@@@notes

* Rather than
* This function completes right away. Web framework then puts a callback on the future
* How do we decide how many concurrent requests we can handle?

@@@@

@@@

@@@section

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/scala/info/batey/sync/SyncWebFramework.scala){#async-request group="scala"}

@@snip[x]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/AsyncWebFramework.java){#async-request2 group="java"}

@@@@notes

* thenCompose - chain on another async comp
* thenApply - transform the result once it is complete

@@@@

@@@


@@@section

## Akka HTTP

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/UserRoute.scala){#user-route group="scala"}

@@snip[x]($root$/../http-streams/src/main/java/j/info/batey/akka/http/UserRoute.java){#user-route group="java"}

@@@

@@@section

## Akka HTTP

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/UserRoute.scala){#user-route }

@@@@notes

* Future rather than a sync method call
 * We can easily add a timeout that Akka HTTP can handle

@@@@

@@@


@@@section

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/DataAccess.scala){#db-call group="scala"}

@@snip[x]($root$/../http-streams/src/main/java/j/info/batey/akka/http/DataAccess.java){#db-call group="java"}


@@@@notes

* Quick look at what an async call to a DB might look like

@@@@

@@@

@@@section

##  Responsiveness under load

@@@@notes

Notes

@@@@

@@@

@@@section

# Playing fair

@@@

@@@section

<img src="response-time.png" style="width: 1000px;"/>

@@@

@@@section { data-background-video="images/fishermen.mp4" }

# @span[OutOfMemoryError]{.orange .fragment}

@@@@notes

* Reactive streams

@@@@

@@@

@@@section

## Queueing and/or buffering

* How would this work if Kafka was between the services?

![queue](pubsub-half.png)

@@@

@@@section

## Fast publisher

* Without the queue:
  * Down stream gets overwhelmed
  * Publisher has wasted resources

![queue](pubsub-full.png)

@@@@notes

* Assumption that kafka never fills up
* Can't do this for in memory, can't do this for requests between services
* Would this work in memory?
* Why produce data no one is ready to consume?

@@@@

@@@

@@@section

## Reacting to failure

* Is the consumer slow or down?
* Circuit breakers

![Circuit breaker](circuit-breaker.png)

@@@

@@@section

## Flow control

* Dynamically adjust the rate based on demand

![queue](demand.png)

@@@



@@@section

# Reactive Streams

@notes[So let's start with Reactive Streams. To better understand the reactive streams initiative it makes sense to look at some history of there this effort came from<br>Time: 11:55]

@@@


@@@section

### So far the good news...

@@snip[x]($root$/../http-streams/src/main/java/rs/async/Asynchronous.java){#actor group="java"}

@@snip[x]($root$/../http-streams/src/main/java/rs/async/Asynchronous.java){#enqueue group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/rs/async/Asynchronous.scala){#actor group="scala"}

@@snip[x]($root$/../http-streams/src/main/scala/rs/async/Asynchronous.scala){#enqueue group="scala"}

@@@@notes

* Async, thread efficiency
* But now when do we stop?

@@@@

@@@



@@@section { data-background="#489ebd" }

### Reactive Streams Timeline

![Reactive Streams Timeline](images/reactive_streams_timeline.png)

@notes[Industry got together under Reactive Streams working group initiated by Viktor Klang of the Akka Team (not limited to JVM)]

@@@

@@@section { data-background="#489ebd" }

### Reactive Streams Scope

Reactive Streams is an initiative to provide a standard for asynchronous stream processing with non-blocking back pressure. This encompasses efforts aimed at runtime environments (JVM and JavaScript) as well as network protocols

http://www.reactive-streams.org

@@@@notes

* For communication between libraries

@@@@

@@@

@@@section

### `java.util.concurrent.Flow`

@@snip[rs]($root$/../http-streams/src/main/java/rs/Publisher.java) { #rs }
@@snip[rs]($root$/../http-streams/src/main/java/rs/Subscriber.java) { #rs }
@@snip[rs]($root$/../http-streams/src/main/java/rs/Subscription.java) { #rs }
@@snip[rs]($root$/../http-streams/src/main/java/rs/Processor.java) { #rs }

@notes[Most significant milestone was the inclusion of the RS interfaces in JDK9
If you're not on JDK9 you can use the org.reactivestreams library.]

@@@

@@@section

### Availablility

Included in JDK9

No JDK9? No problem!

```
<dependency>
  <groupId>org.reactivestreams</groupId>
  <artifactId>reactive-streams</artifactId>
  <version>1.0.2</version>
</dependency>
```

@@@


@@@section

# Akka Streams

@notes[Before we start: hands up Java/Scala experience? Will show a bit of both.<br>Time: 12:05-12:10]

@@@

@@@section

![Source, Flow and Sink](images/stream-blocks.svg)

@notes[starts of our story]

@@@

@@@section

@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#source-no}
@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#flow-no .fragment}
@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#sink-no .fragment}

@notes[Before we dive into the specifics, let's start with some small examples to get a feel]

@@@

@@@section

@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#source}
@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#flow}
@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#sink}
@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#graph .fragment}
@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#run .fragment}

@@@

@@@section

@@snip[x]($root$/../http-streams/src/main/scala/streams/Intro.scala){#source}
@@snip[x]($root$/../http-streams/src/main/scala/streams/Intro.scala){#flow}
@@snip[x]($root$/../http-streams/src/main/scala/streams/Intro.scala){#sink}
@@snip[x]($root$/../http-streams/src/main/scala/streams/Intro.scala){#graph .fragment}
@@snip[x]($root$/../http-streams/src/main/scala/streams/Intro.scala){#run .fragment}

@@@

@@@section

Java:

@@snip[x]($root$/../http-streams/src/main/java/streams/Intro.java){#short}

Scala:

@@snip[x]($root$/../http-streams/src/main/scala/streams/Intro.scala){#short}

@notes[we really try to make the API really easy to use for Java and Scala]


@@@

@@@section

## Materialization

@@snip[x]($root$/../http-streams/src/main/java/streams/Materialization.java){#multiple group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/streams/Materialization.scala){#multiple group="scala"}

@notes[running = 2-stage: build graph, run graph. run = materialization. Same graph can be materialized multiple times.]

@@@


@@@section

## Actor materialization

@@snip[x]($root$/../http-streams/src/main/java/streams/Materialization.java){#fusing group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/streams/Materialization.scala){#fusing group="scala"}

![Source, Flow and Sink](images/stream-unfused.svg)

@notes[Advantage of 2-phase: reusable building blocks, but opportunity for optimizations at materialization time]

@@@

@@@section

# Fusing

@@snip[x]($root$/../http-streams/src/main/java/streams/Materialization.java){#fusing-explicit-async group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/streams/Materialization.scala){#fusing-explicit-async group="scala"}

![Source, Flow and Sink](images/stream-async.svg)

@@@

@@@section

# Fusing

@@snip[x]($root$/../http-streams/src/main/java/streams/Materialization.java){#fusing-async group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/streams/Materialization.scala){#fusing-async group="scala"}

![Source, Flow and Sink](images/stream-async.svg)

@@@

@@@section

## Backpressure propagation

Demand is signalled across async boundaries

![Backpressure across async boundary](images/backpressure-propagation.svg)

@@@@notes

* Upstream stages are not allowwed to do anything until downstream 

@@@@

@@@

@@@section

## Backpressure propagation

Thanks to Reactive Streams, across different libraries:

![Backpressure across async boundary](images/backpressure-propagation-across-libs.svg)

@@@

@@@section

## Backpressure propagation

Often also possible across external protocols, i.e. TCP:

![TCP window](images/tcp-window-initial.png)

@span[![TCP window](images/tcp-window-1.png)]{.fragment}

@@@

@@@section

## Backpressure propagation

Can be seen in e.g. wireshark:

![Wireshark backpressure](images/wireshark-fullwindow-2.png)

@@@@notes

Akka streams backpressure translating to TCP bacck pressurej

@@@@

@@@

@@@section

## TCP windowing

![SS](ss.png)

@@@@notes

- Window fills up, client stops sending
- Heart beats to know when it can start sending again

@@@@

@@@

@@@section

## Putting it all together

HTTP Client -> TCP -> Server -> HTTP Server -> TCP -> Apache Cassandra

@@@@notes

* Goal not to teach you Akka HTTP
* But to show you the benefits of using a library which adheres to flow control

@@@@

@@@

@@@section

## Putting it all together

* Akka HTTP client
* Akka HTTP server
* Alpakka 

@@@@notes

Notes

@@@@

@@@

@@@section

## Akka Client

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/ActivityClient.scala){#client-request group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/ActivityClient.scala){#client group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/ActivityClient.scala){#client-request group="scala"}

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/ActivityClient.scala){#client group="scala"}

@@@@notes

* Important thing to note here is that the entity is a Source
* There is a marshalling toolkit
* Remember the rule,  source can't produce data uness sink demands it
* Until you start consuming the source with a sink, nothing happens (apart from TCP buffers fill up)


@@@@

@@@

@@@section

## Akka Server

```scala
val bound: Future[Http.ServerBinding] =
    Http().bindAndHandle(route, "localhost", 8080)
```

```scala
 def bindAndHandle(
    handler:   Flow[HttpRequest, HttpResponse, Any],
    interface: String,
    port: Int)
```

@@@

@@@section

## Akka Server

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/UserRoute.scala){#stream-route group="scala"}

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/UserRoute.scala){#stream-route group="java"}

@@@@notes

* You provide a source
* Data is not pulled from the source UNTIL there is space in the TCP send buffer
* IF the buffer fills up, we stop reuquesting
* Meaning that we should stop pulling data from the database
* Compare that to the sync model

@@@@

@@@





@@@section

## Cassandra Source 

```scala
CassandraSource(new SimpleStatement(
      "select * from user_tracking where user_id = ?", userId))(session)
     .map(row => Event(
       row.getString("user_id"),
       UUIDs.unixTimestamp(row.getUUID("time")),
       row.getString("event")))
```

@@@@notes

Notes

@@@@

@@@

@@@section

# Demo time

@@@

@@@section

## Demo summary

* HTTP slow client
    * Client makes request for a large payload
    * TCP buffers fill up
    * Server *stops* getting data from database
    * Client then demands more
    * Everything starts flowing


@@@













@@@section

# Re-cap

Backpressure prevents overload

@span[Reactive Streams for integration]{.fragment}

@span[e.g. Akka Streams to implement]{.fragment}

@span[e.g. Akka HTTP to leverage]{.fragment}

@notes[Backpressure protects against overloading the target of an asynchronous non-blocking stream. Reactive Streams makes this work end-to-end, use a library like Akka Streams, Akka HTTP is built on top of Akka Streams and benefits from it.]

@@@

@@@section

## Happy hAkking!

Slides & Code
:  [github.com/chbatey/akka-streams-flow-control-example](https://github.com/chbatey/akka-streams-flow-control-example)

Docs & QuickStarts
:  [akka.io](https://akka.io), [developer.lightbend.com/start](https://developer.lightbend.com/start)

Community
: [gitter.im/akka/akka](https://gitter.im/akka/akka)

Tweet
: [@akkateam](https://twitter.com/akkateam), [@chbatey](https://twitter.com/chbatey)

@@@

@@@section

@@@@notes

Notes

@@@@

@@@






@span[$selectedLanguage$]{#selectedLanguage}

@@@vars
<script>
  const selectedLanguage = document.getElementById('selectedLanguage').innerHTML.toLowerCase()
  const hiddenLanguage = (selectedLanguage == "java") ? "scala" : "java"

  console.log(selectedLanguage)
  var javaFragments = document.getElementsByClassName('group-' + hiddenLanguage)
  while (javaFragments.length > 0) {
    javaFragments[0].remove()
  }
</script>
@@@