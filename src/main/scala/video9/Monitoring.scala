package video9

import akka.actor._

/**
  * Created by mtulaza on 2016-03-18.
  */
class Ares(athena: ActorRef) extends Actor {

  override def preStart() = {
    context.watch(athena)
  }

  override def postStop() = {
    println("Ares postStop...")
  }

  def receive = {
    case Terminated =>
      println("Ares Terminated...")
      context.stop(self)
  }
}

class Athena extends Actor {
  def receive = {
    case msg =>
      println(s"Athena received $msg")
      context.stop(self)
  }
}

object Monitoring extends App {
  val system = ActorSystem("monitoring")

  val athena = system.actorOf(Props[Athena], "athena")
  val ares = system.actorOf(Props(classOf[Ares], athena), "ares")

  athena ! "Hi"

  system.terminate()
}
