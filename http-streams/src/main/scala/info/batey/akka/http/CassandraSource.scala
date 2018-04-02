package info.batey.akka.http

import akka.stream._
import akka.stream.scaladsl.Source
import akka.stream.stage._
import com.datastax.driver.core.{ResultSet, Row, Session, Statement}

import scala.util.{Failure, Success, Try}

object CassandraSource {
  def apply(statement: Statement)(implicit session: Session) = {
    // This makes it very obvious when we start/stop querying Cassandra
    statement.setFetchSize(10)
    Source.fromGraph(new CassandraSource(statement, session))
  }
}

final class CassandraSource(statement: Statement, session: Session) extends GraphStage[SourceShape[Row]] {
  val out: Outlet[Row] = Outlet("CassandraSource.out")
  override val shape: SourceShape[Row] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with StageLogging {
      var lastResult = Option.empty[ResultSet]
      var fetchCB: AsyncCallback[Try[ResultSet]] = _

      override def preStart(): Unit = {
        implicit val ec = materializer.executionContext
        fetchCB = getAsyncCallback[Try[ResultSet]](newCassandraResponse)
        session.executeAsync(statement).asScala.onComplete(fetchCB.invoke)
      }

      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = {
            implicit val ec = materializer.executionContext
            tryPush()
          }
        }
      )

      private def tryPush(): Unit = {
        implicit val ec = materializer.executionContext
        lastResult match {
          case Some(rs) if rs.getAvailableWithoutFetching == 0 =>
            log.debug("Fetching more results from Cassandra")
            rs.fetchMoreResults().asScala.onComplete(fetchCB.invoke)
          case Some(rs) if rs.isExhausted =>
            completeStage()
          case Some(rs) if isAvailable(out) =>
            push(out, rs.one())
            tryPush()
          case _ =>
        }
      }

      private def newCassandraResponse(resultSet: Try[ResultSet]): Unit = {
        resultSet match {
          case Success(rs) =>
            lastResult = Some(rs)
            tryPush()
          case Failure(failure) =>
            failStage(failure)
        }
      }
    }
}

