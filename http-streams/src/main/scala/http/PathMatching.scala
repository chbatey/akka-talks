package sc.http

import akka.http.scaladsl.server.{Directives, PathMatcher1}

object PathMatching extends Directives{
  private[http] def seg(): Unit = {
    //#segment
    val route = path("hello") {
      complete("Hello, World!")
    }
    //#segment
  }

  private[http] def segments(): Unit = {
    //#segments
    val route = path("hello" / Segment) { name =>
      complete("Hello, " + name + "!")
    }
    //#segments
  }

  private[http] def advanced(): Unit = {
    //#advanced
    val matcher: PathMatcher1[Option[Int]] =
      "foo" / "bar" / "X" ~ IntNumber.? / ("edit" | "create")
    //#advanced
  }
}
