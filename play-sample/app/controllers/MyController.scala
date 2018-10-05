package controllers

import akka.stream.Materializer
import info.batey.akka.{GreeterServiceClient, HelloRequest}
import play.api.mvc.{AbstractController, ControllerComponents}
import javax.inject._
import play.api._
import play.api.mvc._


import scala.concurrent.ExecutionContext

@Singleton
class MyController @Inject() (implicit greeterClient: GreeterServiceClient, cc: ControllerComponents, mat: Materializer, exec: ExecutionContext) extends AbstractController(cc) {

  def sayHello(name: String) = Action.async { implicit request =>
    greeterClient.sayHello(HelloRequest(name))
      .map { reply =>
        Ok(s"response: ${reply.message}")
      }
  }

}
