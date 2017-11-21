package info.batey.akka.http

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.utils.UUIDs

object ActivityGenerator extends App {

  val cluster = Cluster.builder()
      .addContactPoint("localhost")
        .build()
  val session = cluster.connect("akka_streams")

  val nrEvents = 1000000000
  val events: Stream[String] = List("go to the shops", "buy crisps", "eat crisps", "nap").toStream #::: events
  val eventsIt = events.iterator

  (0 until nrEvents) foreach { _ =>
    session.executeAsync("insert into user_tracking(user_id, time, event) values (?, ?, ?)",
      "chbatey", UUIDs.timeBased(), eventsIt.next())
  }

  println("All done")
  cluster.close()
}
