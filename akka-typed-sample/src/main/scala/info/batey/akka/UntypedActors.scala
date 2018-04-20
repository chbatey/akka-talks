package info.batey.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object LockProtocol {
  //#protocol
  sealed trait LockProtocol
  final case object Lock extends LockProtocol
  final case object Unlock extends LockProtocol

  sealed trait LockStatus
  final case object Granted extends LockStatus
  final case class Taken(who: ActorRef) extends LockStatus
  //#protocol
}

object UntypedActors {
  val system = ActorSystem()
  system.actorOf(Props[MutableActor], "chris-lock")
}

import LockProtocol._

//#mutable
class MutableActor extends Actor with ActorLogging {

  private var owner: Option[ActorRef] = None

  def receive: Receive = {
    case Lock if owner.isEmpty =>
      owner = Some(sender())
      sender() ! Granted
    case Lock =>
      sender() ! Taken(owner.get)
    case Unlock =>
      require(owner.contains(sender()))
      owner = None
  }
}
//#mutable

import LockProtocol._

//#become
class BecomeActor extends Actor {
  private val unlocked: Receive = {
    case Lock =>
      sender() ! Granted
      context.become(locked(sender()))
  }
  private def locked(who: ActorRef): Receive = {
    case Lock =>
      sender() ! Taken(who)
    case Unlock =>
      require(sender() == who)
      context.become(unlocked)
  }
  override def receive: Receive = unlocked
}
//#become
