package info.batey.akka.http

object Domain {
  type UserId = String
  type EventPayload = String
  type Name = String

  case class User(userId: UserId, name: Name, age: Int)
  case class Event(userId: UserId, time: Long, info: EventPayload)
}
