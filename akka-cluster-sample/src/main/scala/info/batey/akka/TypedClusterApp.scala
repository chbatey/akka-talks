package info.batey.akka.typed

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.AskPattern._
import akka.cluster.typed.{Cluster, ClusterSingleton, ClusterSingletonSettings}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import info.batey.akka.typed.TypedCounter._

import scala.concurrent.Future
import scala.concurrent.duration._

object Common {
  val config = ConfigFactory.parseString(
    """
akka {
  loglevel = "DEBUG"

  actor {
    provider = "cluster"
  }

  remote {
    artery {
       enabled = on
       transport = tcp
       canonical.hostname = "127.0.0.1"

       advanced {
        flight-recorder {
          destination = /tmp/
          enabled = on
        }
       }
    }
  }

  cluster {
    seed-nodes = [
      "akka://system@127.0.0.1:2551"
    ]
  }
}
    """)

  val dcA = ConfigFactory.parseString(
    """
akka {
//#dc-config
  cluster.multi-data-center.self-data-center = "a"
//#dc-config

  remote.artery.canonical {
    port = 2551
  }
}
    """).withFallback(config)

  val dcB = ConfigFactory.parseString(
    """
akka {
//#dc-config-b
  cluster.multi-data-center.self-data-center = "b"
//#dc-config-b

  remote.artery.canonical {
    port = 2552
  }
}
    """).withFallback(config)


}

object TypedCounter {
  //#protocol
  trait CounterCommand
  case object Increment extends CounterCommand
  final case class GetValue(replyTo: ActorRef[Int]) extends CounterCommand
  case object GoodByeCounter extends CounterCommand

  def counter(value: Int): Behavior[CounterCommand] = Behaviors.receiveMessage[CounterCommand] {
    case Increment ⇒
      counter(value + 1)
    case GetValue(replyTo) ⇒
      replyTo ! value
      Behaviors.same
  }
  //#protocol
}

object TypedClusterAppDcA extends App {

  import Common._

  val system = ActorSystem(Behaviors.empty[String], "system", dcA)
  implicit val timeout = Timeout(1.second)
  implicit val scheduler = system.scheduler
  implicit val ec = system.executionContext

  val cluster = Cluster(system)
  val selfDc = cluster.selfMember.dataCenter

  println(s"Up and running in dc $selfDc")

  //#extension
  val clusterSingleton = ClusterSingleton(system)
  //#extension

  //#singleton
  val singleton: ActorRef[CounterCommand] =
    clusterSingleton.spawn[CounterCommand](TypedCounter.counter(0),
      "cat-counter",
      Props.empty,
      ClusterSingletonSettings(system),
      GoodByeCounter)
  //#singleton

  //#messages
  singleton ! Increment
  singleton ! Increment
  val count: Future[Int] = singleton ? GetValue
  //#messages

  count.onComplete(println)

}


object TypedClusterAppDcB extends App {

  import Common._

  println("Up and running")

  val system = ActorSystem(Behaviors.empty[String], "system", dcB)
  implicit val timeout = Timeout(10.second)
  implicit val scheduler = system.scheduler
  implicit val ec = system.executionContext

  val cluster = Cluster(system)
  val selfDc = cluster.selfMember.dataCenter

  println(s"Up and running in dc $selfDc")

  val clusterSingleton = ClusterSingleton(system)

  //#proxy
  val proxy = clusterSingleton.spawn[CounterCommand](
    TypedCounter.counter(0),
    "cat-counter",
    Props.empty,
    ClusterSingletonSettings(system)
      .withDataCenter("a")
    ,
    GoodByeCounter
  )
  //#proxy

  //#proxy-send
  proxy ! Increment
  proxy ! Increment
  val count: Future[Int] = proxy ? GetValue
  //#proxy-send

  count.onComplete(println)

}
