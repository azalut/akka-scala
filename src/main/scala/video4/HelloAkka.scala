package video4

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by mtulaza on 2016-03-18.
  */
case class WhoToGreet(who: String)

class Greeter extends Actor {
  def receive = {
    case WhoToGreet(who) => println(s"Hello $who")
  }
}

object HelloAkka extends App {
  val system = ActorSystem("Hello-Akka")

  val greeter = system.actorOf(Props[Greeter], "greeter")

  greeter ! WhoToGreet("Akka")
}
