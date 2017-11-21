package info.batey.akka.http

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.core.{ResultSet, Session, SimpleStatement}
import com.typesafe.scalalogging.LazyLogging
import info.batey.akka.http.Domain.{Event, User, UserId}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

object DataAccess extends LazyLogging {
  /**
    * An example of a call to Cassandra that will return one row. No need
    * to stream, a Future will do.
    */
  def lookupUser(userId: UserId)(implicit session: Session, ec: ExecutionContext): Future[Option[User]] = {
    session.executeAsync("select * from users")
      .map((rs: ResultSet) => Option(rs.one())
        .map(row => User(
          row.getString("user_id"),
          row.getString("user_name"),
          row.getInt("age"))))
  }

  /**
    * Example of a call to Cassandra that could return billions of rows.
    * This pulls them into memory. Don't do this.
    */
  @deprecated("Sorry we did this", "always")
  def lookupEvents1(userId: UserId)(implicit session: Session, ec: ExecutionContext): Future[Seq[Event]] = {
    session.executeAsync("select * from user_tracking where name = ?", userId)
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
  def lookupEvents(userId: UserId)(implicit session: Session, ec: ActorMaterializer): Source[Event, NotUsed] = {
    CassandraSource(new SimpleStatement("select * from user_tracking where user_id = ?", userId))
      .map(row => Event(
        row.getString("user_id"),
        UUIDs.unixTimestamp(row.getUUID("time")),
        row.getString("event")))
      .recover {
      case e: Throwable =>
        // TODO deal with failures
        logger.error("Stream failed: ", e)
        Event("This is not the event you are looking for", 1, "Oh no")
    }
  }
}
