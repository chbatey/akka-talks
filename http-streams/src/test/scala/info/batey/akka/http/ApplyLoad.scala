package info.batey.akka.http

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

// What does a load test tool do?
// Gatling vs JMeter
class ApplyLoad extends Simulation {
  val httpConf = http.baseURL("http://localhost:8080").shareConnections

  // Each simulated user performs a request every second:
  val scn = scenario("Basic load")
    .exec(http("request").get("/user/chbatey"))

  //  val scn = scenario("Basic load")
  //    .exec(http("request").get("/user-no-cass/chbatey"))


  // Simulate users:
  //  setUp(scn.inject(rampUsers(40).over(10.seconds))
  //    .protocols(httpConf))

  setUp(scn.inject(
    constantUsersPerSec(400).during(60.seconds))
    .protocols(httpConf))
}
