package models

import java.sql.ResultSet

import akka.actor._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class DBActor extends Actor {
  override def receive: Actor.Receive = {
    case _ => sender ! "got something"
  }
}

trait DBResponse {
  def toJson: JsValue
}

class WebSocketChannel(wsChannel: Concurrent.Channel[JsValue])
  extends Actor with ActorLogging {

  val backend = Akka.system.actorOf(Props(classOf[DBActor]))

  def convertJson(x: JsValue) = {
    x\\"msg"
  }

  def receive: Actor.Receive = {
    case jsRequest: JsValue =>
      backend ! convertJson(jsRequest)
    case x: DBResponse =>
      wsChannel.push(x.toJson)
  }

}

object WebSocketChannel {
  def props(channel: Concurrent.Channel[JsValue]): Props =
    Props(classOf[WebSocketChannel], channel)

  def init: (Iteratee[JsValue, _], Enumerator[JsValue]) = {

    var actor: ActorRef = null
    val out = Concurrent.unicast[JsValue] {
      channel =>
        actor = Akka.system.actorOf(WebSocketChannel.props(channel))
    }

    val in = Iteratee.foreach[JsValue] {
      jsReq => actor ! jsReq
    }
    (in, out)
  }
}