package info.batey.akka.http

object Domain {
  type UserId = String
  type EventPayload = String
  type Name = String

  case class User(userId: UserId, name: Name, age: Int) {
    def serialise: String = toString
  }
  case class Event(userId: UserId, time: Long, info: EventPayload)
}
