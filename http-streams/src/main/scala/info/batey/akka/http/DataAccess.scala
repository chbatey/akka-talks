package info.batey.akka.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.core.{Cluster, ResultSet, SimpleStatement}
import com.typesafe.scalalogging.LazyLogging
import info.batey.akka.http.Domain.{Event, User, UserId}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._


object DataAccess extends LazyLogging {
  val cluster = Cluster.builder().addContactPoint("localhost").build()
  val session = cluster.connect("akka_streams")

  /**
    * Used to test Akka HTTP without a local Cassandra. But still simulate
    * response time
    */
  def loopupUserStub(userId: UserId)(implicit system: ActorSystem): Future[Option[User]] = {
    val promise = Promise[Option[User]]()
    system.scheduler.scheduleOnce(200.millis, new Runnable {
      override def run(): Unit =
        promise.success(Some(User(userId, "Christopher", 32)))
    })
    promise.future
  }

  /**
    * Used to simulate a blocking call. Will not work for a large number of concurrent requests.
    * Don't do this at home.
    */
  def loopupUserBlocking(userId: UserId)(implicit system: ActorSystem): Future[Option[User]] = {
    Thread.sleep(200)
    Future.successful(Some(User(userId, "Christopher", 32)))
  }

  /**
    * An example of a call to Cassandra that will return one row. No need
    * to stream, a Future will do.
    */
  //#db-call
  def lookupUser(userId: UserId): Future[Option[User]] =
    session.executeAsync("select * from users where user_id = ?", userId).asScala
      .map((rs: ResultSet) => Option(rs.one())
        .map(row => User(
          row.getString("user_id"),
          row.getString("user_name"),
          row.getInt("age"))))
  //#db-call

  /**
    * Example of a call to Cassandra that could return billions of rows.
    * This pulls them into memory.
    * Don't do this at home.
    */
  @deprecated("Sorry we did this", "always")
  def lookupEvents1(userId: UserId): Future[Seq[Event]] = {
    session.executeAsync("select * from user_tracking where name = ?", userId).asScala
      .map(rs => rs.all().asScala.map(row => {
        Event(
          row.getString("user_id"),
          UUIDs.unixTimestamp(row.getUUID("time")),
          row.getString("event"))
      }))
  }

  /**
    * Example of a call to Cassandra that could return billions of rows.
    * Uses streams to pull results from Cassandra as here is demand.
    */
  //#lookup
  def lookupEvents(userId: UserId)(implicit ec: ActorMaterializer): Source[Event, NotUsed] = {
    // Fancy Alpakka
    CassandraSource(new SimpleStatement("select * from user_tracking where user_id = ?", userId))(session)
      .map(row => Event(
        row.getString("user_id"),
        UUIDs.unixTimestamp(row.getUUID("time")),
        row.getString("event")))
  }
  //#lookup
}
