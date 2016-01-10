package controllers

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor, Props}
import models.WebSocketChannel
import play.api.libs.iteratee.{Enumerator, Concurrent, Iteratee}
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller {

  def index = Action {
    request =>
      Ok(views.html.main())
  }

  def websocketBroadcast = WebSocket.using[String] {
    request =>
      val (out, channel) = Concurrent.broadcast[String]
      val in = Iteratee.foreach[String] {
        word => channel.push(word.reverse)
      }

      (in, out)
  }

  def websocketUnicast = WebSocket.using[String] {
    request =>
      var channel: Concurrent.Channel[String] = null
      val out = Concurrent.unicast[String] {
        ch =>
          channel = ch
      }
      val in = Iteratee.foreach[String] {
        word => channel.push(word.reverse)
      }
      (in, out)
  }

  def dbWebsocket = WebSocket.using[JsValue] {
    request =>
      WebSocketChannel.init
  }


  class Reverser(outChannel: ActorRef) extends Actor {

    def receive = {
      case s: String => outChannel ! s.reverse
    }
  }

  object Reverser {
    def props(outChannel: ActorRef) = Props(classOf[Reverser], outChannel)
  }

  def websocket = WebSocket.acceptWithActor[String, String] {
    request => out =>
      Reverser.props(out)
  }

  class ReverserWithLimit(min: Int, max: Int) extends Actor {

    def receive = {
      case s: String if s.length > min & s.length < max => println(s.reverse)
      case _ => println(s"Sorry, didnt quite understand that. I can only process a String of length $min-$max.")
    }
  }

  object ReverserWithLimit {
    def props(min: Int, max: Int) = Props(classOf[Reverser], min, max)
  }

  case class WebsocketRequest(reqType: String, message: String)

  class RandomActor extends Actor {
    override def receive: Receive = {
      case s: Any => println(Math.random())
    }
  }

  object RandomActor {
    def props = Props(classOf[RandomActor])
  }

  implicit val requestFormat = Json.format[WebsocketRequest]
  implicit val requestFrameFormatter = FrameFormatter.jsonFrame[WebsocketRequest]

  def websocketFormatted = WebSocket.acceptWithActor[WebsocketRequest, JsValue] {
    request => out =>
      Props(classOf[RandomActor])
  }

  def wsheaders = WebSocket.tryAcceptWithActor[String, String] {
    request =>
      Future.successful(request.headers.get("token") match {
        case Some(x) => Right(out => Reverser.props(out))
        case _ => Left(Forbidden)
      })
  }

  def wsWithHeader = WebSocket.tryAccept[String] {
    rh =>
      Future.successful(rh.headers.get("token") match {
        case Some(x) =>
          var channel: Concurrent.Channel[String] = null
          val out = Concurrent.unicast[String] {
            ch =>
              channel = ch
          }
          val in = Iteratee.foreach[String] {
            word => channel.push(word.reverse)
          }
          Right(in, out)
        case _ => Left(Forbidden)
      })
  }

}
