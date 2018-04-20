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

@span[Typed refinements]{ .fragment }

@span[Multi DC]{ .fragment }

@span[gRPC]{ .fragment }

@span[Artery TCP]{ .fragment }

@span[Stream Refs]{ .fragment }

@@@

@@@section

## Akka Typed

@@@@notes

* Hrmm

@@@@

@@@


@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/UntypedActors.scala){ #protocol }

@@@@notes

TODO

@@@@

@@@

@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/UntypedActors.scala){ #mutable }

@@@@notes

TODO

@@@@

@@@

@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/UntypedActors.scala){ #become }

@@@@notes

TODO

@@@@

@@@

@@@section

## Akka Typed

* `ActorRef` becomes `ActorRef[T]`
* No more Actor trait
* No more `sender()`
* No more `actorSelection`

@@@@notes

Notes

@@@@

@@@

@@@section

## Akka Typed

* Send 0+ messages
* Spawn 0+ children
* Change its behavior

@@@@notes

TODO

@@@@

@@@

@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #protocol }
@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #protocol-return .fragment }

@@@@notes

TODO

@@@@

@@@

@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #state-locked }
@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #state-unlocked .fragment }

@@@@notes

TODO

@@@@

@@@

@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #top-level }

@@@@notes

TODO

@@@@

@@@

@@@section

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #needs-lock-instance  }
@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #needs-lock .fragment }

@@@@notes

TODO

@@@@

@@@

@@@section

## Running

@@snip[x]($root$/../akka-typed-sample/src/main/scala/info/batey/akka/TypedActors.scala){ #running  }

@@@@notes

TODO

@@@@

@@@


@@@section

## Persistence

```scala
case class Command(data: String)
case class Event(data: String)
case class State(events: List[String] = Nil)
```

```scala
val behavior: Behavior[Command] =
  PersistentBehaviors.receive[Command, Event, State](
    persistenceId = "abc",
    initialState = State(),
    commandHandler = (ctx, state, cmd) ⇒ ???,
    eventHandler = (state, evt) ⇒ ???)
```


@@@@notes

Notes

@@@@

@@@


@@@section

## Command Handler

```scala
val commandHandler: CommandHandler[Command, Event, State] =
  CommandHandler.command {
    case Cmd(data) ⇒ Effect.persist(Evt(data))
  }
```

* Persist
* PersistAll
* Stop
* Unhandled


@@@@notes

Notes

@@@@

@@@

@@@section

## Event Handler

```scala
val eventHandler: (State, Event) ⇒ (State) = {
  case (state, Evt(data)) ⇒ state.copy(data :: state.events)
}

```

@@@@notes

Notes

@@@@

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
* UnreachableDatacenter

@@@@

@@@

@@@section

## Sharding and singletons

@@@

@@@section

## Data Center A

@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #dc-config }

@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #extension .fragment }

@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #singleton .fragment }

@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #messages .fragment }

@@@@notes

Notes

@@@@

@@@


@@@section

## Data Center B

@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #dc-config-b }
@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #proxy .fragment }
@@snip[x]($root$/../akka-cluster-sample/src/main/scala/info/batey/akka/TypedClusterApp.scala){ #proxy-send .fragment }

@@@@notes

Notes

@@@@

@@@

@@@section

TODO: Maybe put in sharding?

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

## Akka gRPC

@@@@notes

Notes

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
