@@@section { data-background="#15a9ce" }

### State of Akka 

##### Christopher Batey (@chbatey)
##### Software Engineer - Akka team

@@@@notes

* Me
* Lightbend: Scala, Akka, Play

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

## New in Akka

Multi DC

@span[gRPC]{ .fragment }

@span[Artery TCP]{ .fragment }

@span[Stream Refs]{ .fragment }

@span[Typed refinements]{ .fragment }

@span[Management (maybe)]{ .fragment }

@@@


@@@section

##Multi DC
@span[Running Akka cluster across DCs] { .fragment }

@@@

@@@section

## Why?

@span[Cost of WAN]{ .fragment }

@span[Membership management during partitions]{ .fragment }

@span[Singletons and sharding]{ .fragment }

@span[Distributed data]{ .fragment }

@@@

@@@section

![dc](images/cluster-dc.png)

@@@@notes

* Failure detection
* UnreachableDatacentre

@@@@

@@@

@@@section

## Sharding and singletons

TODO example of how the proxy works

@@@

@@@section

## Artery

@@@

@@@section

## Artery

* Aeron based artery is UDP based
* New TCP Artery uses Streams TCP
* Supports TLS

@@@@notes

* Requires full cluster restart

@@@@

@@@

@@@section

## Artery advantages

* Control message stream
* Large message stream
* Mostly allocation free
* Built in flight recorder

@@@

@@@section

TODO show flight recorder

@@@


@@@section

## Stream refs

@span[Reactive streams over the network]{ .fragment }

@@@

@@@section

![dc](images/AkkaState-streamrefs.svg)

@@@

@@@section

![dc](images/toomuchmail.jpg)

@@@

@@@section

![dc](images/backpressure-propagation.svg)

@@@


@@@section

@@snip[x]($root$/../akka-overview/src/main/scala/info/batey/akka/streams/StreamRefs.scala){ #source-ref }

@@@

@@@section

## SourceRef

```scala
trait SourceRef[T] {
  def source: Source[T, NotUsed]
}
```

@@@

@@@section

@@snip[x]($root$/../akka-overview/src/main/scala/info/batey/akka/streams/StreamRefs.scala){ #source-ref-run }

@@@

@@@section

## Under the hood

* Message ordering
* Demand propagation
* Subscription timeouts

@@@@notes

* Messages are ordered
* Demand redelivery
* If subscription timeout the materialization will fail

@@@@

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

# Questions?

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
