package info.batey.akka.untyped

import akka.actor.{Actor, ActorSystem, PoisonPill, Props, Timers}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.cluster.typed.ClusterSingleton
import akka.management.AkkaManagement
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class Silly extends Actor with Timers {
  timers.startPeriodicTimer("hello", "hello", 1.second)
  override def receive: Receive = {
    case msg => println(msg)
  }
}

object ClusterApp1 extends App {
  val config = ConfigFactory.load("split-brain.conf")
  val as = ActorSystem("ClusterSystem", config)
  AkkaManagement(as).start()
  as.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props(classOf[Silly]),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(as)),
    name = "consumer")

}

object ClusterApp2 extends App {
  val config =
    ConfigFactory.parseString(
      """
        akka.remote.netty.tcp.hostname = "127.0.0.2"
        akka.remote.netty.tcp.port = 2552
        akka.management.http.hostname = "127.0.0.2"
      """).withFallback(ConfigFactory.load("split-brain.conf"))
  val as = ActorSystem("ClusterSystem", config)
  AkkaManagement(as).start()
  as.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props(classOf[Silly]),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(as)),
    name = "consumer")
}

object ClusterApp3 extends App {
 val config =
    ConfigFactory.parseString(
      """
        akka.remote.netty.tcp.hostname = "127.0.0.3"
        akka.remote.netty.tcp.port = 2553
        akka.management.http.hostname = "127.0.0.3"
      """).withFallback(ConfigFactory.load("split-brain.conf"))
  val as = ActorSystem("ClusterSystem", config)
  AkkaManagement(as).start()
  as.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props(classOf[Silly]),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(as)),
    name = "consumer")
}
