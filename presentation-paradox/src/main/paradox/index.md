@@@section { data-background="#15a9ce" }

### Responsive, back-pressured services with Akka 

##### Christopher Batey

@@@

@@@section

## About me

* Christopher Batey
* Work @ Lightbend on the Akka team
    * Akka
    * Akka Streams
    * Akka Http
    * Akka Persistence for Apache Cassandra

@@@@notes

Notes

@@@@


@@@

@@@section

## Akka toolkit

Actor model

@span[Clustering]{ .fragment }

@span[Persistence (Event Sourcing)]{ .fragment }

@span[HTTP]{ .fragment }

@span[Streams]{ .fragment }

@span[[Reactive Manifesto](https://www.reactivemanifesto.org)]{ .fragment }

@notes[So the actor is the basic building block in Akka, but the library contains more: it turned out that the abstractions for concurrency and resiliency in the actor model lend themselves very well for modelling distributed systems, so Akka grew modules for clustering, persistence, and a HTTP stack.<br>Excellent fit for building systems that have the properties of the Reactive Manifesto]

@@@


@@@section

## Responsive
## Scalable

@@@@notes

- High level goals
- Everyone: Responsive even for low throughput
- Scalable for those who need it
- Scalable in process: Execution model appropriate
- Scalable across machines
- Resource efficiency


@@@@

@@@

@@@section

## Asynchronous
## Back pressured

@@@@notes

- Techniques to achieve the goals
- Different/better techniques?
- Async
  - Remaining responsive
- Back pressure/FlowControl
  - Dealing with components that run at different speeds
  - Much more important when moving away from thread per request

@@@@

@@@


@@@section

## Goals

* What does async give us?
* What does flow control give us?
* Flow control with Akka streams
* Http Client -> TCP -> Http Server -> TCP -> Apache Cassandra (slow client)

@@@@notes

- Takeaways for this presentation
- Flow control in your application and between them
- TCP windowing, receive and send buffers, akka streams
- Big point: If the client slows down we stop fetching results
  from the database. No wasted effort. Constant memory footprint

@@@@

@@@

@@@section

## Use case

* HTTP Service, endpoints for:
    * User information from database
    * Getting user activity over a large time span
    * Constant memory footprint
@@@

@@@section

## Requirements 

* Respond in a timely manner, even if it is a failure
* Don't do any unnecessary work
* Constant memory footprint

@@@


@@@section

## Responsiveness
* Control over when your application responds
    * Service time vs response time
    * Queues and buffers
* Dependencies
    * Don't make their problem your problem

@@@

@@@section

<img src="response-time.png" style="width: 1000px;"/>

@@@

@@@section

## Execution Models 

@@@






@@@section

### Traditional synchronous model

@@snip[Synchronous.java]($root$/../http-streams/src/main/java/rs/async/Synchronous.java){#service}

@@snip[Synchronous.java]($root$/../http-streams/src/main/java/rs/async/Synchronous.java){#perform}


@notes[As a refresher on the advantages of async code I have a tiny example. This should look familiar to most of you, right? a number of tasks are performed by a service]

@@@

@@@section


@@snip[SynWebService.scala]($root$/../sync-examples/src/main/scala/info/batey/sync/SyncWebFramework.scala){#sync-post group="scala"}

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/SyncWebFramework.java){#sync-post group="java"}

@@@@notes

Notes

@@@@

@@@

@@@section

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/scala/info/batey/sync/SyncWebFramework.scala){#sync-post-times group="scala"}

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/SyncWebFramework.java){#sync-post-times group="java"}

@@@@notes

Notes

@@@@

@@@


@@@section

### Asynchronous actor approach

@@snip[x]($root$/../http-streams/src/main/java/rs/async/Asynchronous.java){#actor group="java"}

@@snip[x]($root$/../http-streams/src/main/java/rs/async/Asynchronous.java){#enqueue group="java"}

@@snip[x]($root$/../http-streams/src/main/scala/rs/async/Asynchronous.scala){#actor group="scala"}

@@snip[x]($root$/../http-streams/src/main/scala/rs/async/Asynchronous.scala){#enqueue group="scala"}

@@@@notes

Same trivial snippet in Actor. Main diff: task in mailbox. Saves threads.

Resilience. Back a slide, highlight the sending thread no longer has to deal with the exception

Coordination also much easier when using message passing

Nothing new: smalltalk, erlang

Note: there are of course many ways to make a system asynchronous, and I'm sure many of you have introduced asynchronous boundaries on your own perhaps even with using a library. Actors are just a particularly nice way to achieve it.

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

Notes

@@@@

@@@

@@@section

@@snip[SynWebService.scala]($root$/../sync-examples/src/main/scala/info/batey/sync/SyncWebFramework.scala){#async-request group="scala"}


@@snip[x]($root$/../sync-examples/src/main/java/jdoc/info/batey/sync/AsyncWebFramework.java){#async-request2 group="java"}

@@@@notes

Notes

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

@@@


@@@section

@@snip[x]($root$/../http-streams/src/main/scala/info/batey/akka/http/DataAccess.scala){#db-call group="scala"}

@@snip[x]($root$/../http-streams/src/main/java/j/info/batey/akka/http/DataAccess.java){#db-call group="java"}


@@@@notes

Notes

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

@@@@notes

Notes

@@@@

@@@

@@@section { data-background-video="images/fishermen.mp4" }

# @span[OutOfMemoryError]{.orange .fragment}

@notes[Akka is by no means the only approach to asynchronous programming: Node.js, RxJava for example also exploring the same space. And they all ran into the problem of message targets not being able to keep up. Not impossible to solve, but solutions (e.g. ack'ing etc) ad hoc and not composable. This lead to a number of players in industry identifying the need for an interoperable mechanism to get asynchronous, backperssured streams: the Reactive Streams initiative]

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

@notes[and keeps heartbeating]

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

@@@

@@@section

## Putting it all together

* Akka HTTP client
* Akka HTTP server
*  Alpakka 

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

Notes

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

Notes

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