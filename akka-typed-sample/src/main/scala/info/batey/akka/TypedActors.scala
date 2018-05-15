package info.batey.akka

import akka.actor.typed.receptionist.Receptionist.{Find, Register}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl._
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.util.Timeout
import info.batey.akka.TypedActors._
import info.batey.akka.TypedLockProtocol._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object TypedLockProtocol {
  //#protocol
  sealed trait LockProtocol
  case class Lock(ref: ActorRef[LockStatus]) extends LockProtocol
  case class Unlock(ref: ActorRef[LockStatus]) extends LockProtocol
  //#protocol

  //#protocol-return
  sealed trait LockStatus
  case object Granted extends LockStatus
  case class Taken(who: ActorRef[LockStatus]) extends LockStatus
  case object Released extends LockStatus
  case object UnlockFailed extends LockStatus
  //#protocol-return
}

object TypedActors {

  //#state-locked
  def locked(by: ActorRef[LockStatus]): Behavior[LockProtocol] =
    Behaviors.receiveMessage[LockProtocol] {
      case (Lock(who)) =>
        who ! Taken(by)
        Behaviors.same
      case (Unlock(who)) =>
        require(who == by)
        who ! Released
        unlocked
    }
  //#state-locked

  //#state-unlocked
  val unlocked: Behavior[LockProtocol] =
    Behaviors.receiveMessagePartial[LockProtocol] {
      case (Lock(who)) =>
        who ! Granted
        locked(who)
    }
  //#state-unlocked

}

object Example extends App {
  implicit val timeout = Timeout(1.second)

  val topLevel = Behaviors.setup[LockStatus] { initialCtx =>
    val lock = initialCtx.spawn(unlocked, "lock-a")
    lock ! Lock(initialCtx.self)

    Behaviors.receive {
      case (ctx, Granted) =>
        ctx.log.info("Yay I have the Lock")
        ctx.ask(lock)(Unlock) {
          case Success(m) => m
          case Failure(t) => UnlockFailed
        }
        Behaviors.same
      case (ctx, Taken(who)) =>
        ctx.log.info("Who date take my lock???")
        Behaviors.same
      case (ctx, Released) =>
        ctx.log.info("No more lock for me :(")
        Behaviors.same
    }
  }

  val system = ActorSystem(topLevel, "TopLevel")
}

object ReceptionistExample extends App {
  implicit val timeout = Timeout(1.second)

  sealed trait NeedsLock
  final case class LockActorAvailable(lock: ActorRef[LockProtocol]) extends NeedsLock
  final case object LockNotAvailable extends NeedsLock
  final case object LockGranted extends NeedsLock

  val needsLockGrant = Behaviors.receive[NeedsLock] {
    case (ctx, LockGranted) =>
      ctx.log.info("Yay lock, but what do I do with it??")
      Behaviors.same
    case (ctx, LockNotAvailable) =>
      ctx.log.info("Sad panda")
      Behaviors.stopped
  }

  private def hasListing(key: ServiceKey[LockProtocol], listing: Receptionist.Listing): Boolean = {
    listing.serviceInstances(key).size == 1
  }

  //#needs-lock-instance
  val needsLockInstance = Behaviors.setup[NeedsLock] { initialCtx =>
    val key = ServiceKey[LockProtocol]("lock-a")
    initialCtx.ask(initialCtx.system.receptionist)(Find(key)) {
      case Success(listing) if hasListing(key, listing) =>
        LockActorAvailable(listing.serviceInstances(key).head)
      case _ =>
        LockNotAvailable
    }
    //#needs-lock-instance

    //#needs-lock
    Behaviors.receive[NeedsLock] {
      case (ctx, LockActorAvailable(lockActor)) =>
        ctx.log.info("Lock actor is available, time to get it")
        ctx.ask(lockActor)(Lock) {
          case Success(l) => LockGranted
          case Failure(t) => LockNotAvailable
        }
        needsLockGrant
      case (ctx, LockNotAvailable) =>
        ctx.log.info("Oh noes, no lock actor")
        Behaviors.stopped
    }
    //#needs-lock
  }


  //#top-level
  val topLevel = Behaviors.setup[NeedsLock] { ctx =>
    val lock = ctx.spawn(unlocked, "lock-a")
    ctx.system.receptionist !
      Register(ServiceKey[LockProtocol]("lock-a"), lock)
    needsLockInstance
  }
  //#top-level

  //#running
  val system = ActorSystem(topLevel, "TopLevel")
  //#running
}
