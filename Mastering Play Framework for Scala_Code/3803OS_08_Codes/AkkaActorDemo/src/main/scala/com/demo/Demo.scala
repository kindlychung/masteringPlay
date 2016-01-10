package com.demo

import akka.actor.{Props, ActorSystem, Actor}

class Reverser extends Actor {

  def receive = {
    case s: String => println(s.reverse)
    case _ => println("Sorry, didnt quite understand that. I can only process a String.")
  }
}

object Reverser {
  def props = Props(classOf[Reverser])
}

object Main extends App {
  val system = ActorSystem("demoSystem")
  val demoActor = system.actorOf(Reverser.props, name = "demoActor")
  demoActor ! "Hello, How do u do?"
  demoActor ! "Been Long since we spoke"
  demoActor ! 12345
}
