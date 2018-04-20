package info.batey.akka

import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.Cluster
import akka.pattern.ask
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import info.batey.akka.Counter.{GetValue, GoodByeCounter, Increment}

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
  cluster.multi-data-center.self-data-center = "a"

  remote.artery.canonical {
    port = 2551
  }
}
    """).withFallback(config)

  val dcB = ConfigFactory.parseString(
    """
akka {
  cluster.multi-data-center.self-data-center = "b"

  remote.artery.canonical {
    port = 2552
  }
}
    """).withFallback(config)


}

object Counter {
  trait CounterCommand
  case object Increment extends CounterCommand
  case object GetValue extends CounterCommand
  case object GoodByeCounter extends CounterCommand
}

class Counter extends Actor {

  import Counter._

  override def receive: Receive = counter(0)

  def counter(count: Int): Receive = {
    case Increment =>
      context.system.log.info("Incrementing counter from {}", sender())
      context.become(counter(count + 1))
    case GetValue =>
      context.system.log.info("Sending value to {}", sender())
      sender() ! count
  }
}

object ClusterAppDcA extends App {

  val system = ActorSystem("system", Common.dcA)
  implicit val timeout = Timeout(5.second)
  implicit val ec = system.dispatcher

  val cluster = Cluster(system)
  val selfDc = cluster.selfMember.dataCenter

  println(s"Up and running in dc $selfDc")

  system.actorOf(
    ClusterSingletonManager.props(
      Props(classOf[Counter]),
      GoodByeCounter,
      ClusterSingletonManagerSettings(system),
    ), "counter")

  val proxy = system.actorOf(
    ClusterSingletonProxy.props(
      singletonManagerPath = "/user/counter",
      settings = ClusterSingletonProxySettings(system)),
    name = "counterProxy")

  proxy ! Increment
  proxy ! Increment
  val value = (proxy ? GetValue).mapTo[Int]
  value.onComplete(println)

}

object ClusterAppDcB extends App {

  val system = ActorSystem("system", Common.dcB)
  implicit val timeout = Timeout(20.second)
  implicit val ec = system.dispatcher

  val cluster = Cluster(system)
  val selfDc = cluster.selfMember.dataCenter

  println(s"Up and running in dc $selfDc")

  val proxy = system.actorOf(
    ClusterSingletonProxy.props(
      singletonManagerPath = "/user/counter",
      settings = ClusterSingletonProxySettings(system)
        .withDataCenter("a")),
    name = "counterProxy")

  proxy ! Increment
  proxy ! Increment
  system.log.info("Sending request")
  val value = (proxy ? GetValue).mapTo[Int]
  value.onComplete(println)

}

