package sc.rs.async

import akka.actor.{Actor, ActorRef}

object Asynchronous {

  case class Task()

  private[async] class Result {}

  //#actor
  class MyActor extends Actor {
    override def receive = {
      case Task(/*params*/) ⇒ /* do work */
    }
  }
  //#actor

  def performTasks(): Unit = {
    val actor: ActorRef = null
    val tasks: Seq[Task] = null
    //#enqueue
    for (task <- tasks) {
      actor ! task
    }
    //#enqueue
  }
}
