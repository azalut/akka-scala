package video9

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart, Resume}
import akka.actor._
import video9.Aphrodite.{RestartException, StopException, ResumeException}

/**
  * Created by mtulaza on 2016-03-18.
  */
object Aphrodite {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception
}

class Aphrodite extends Actor {
  override def preStart() = {
    println("Aphrodite preStart hook...")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    println("Aphrodite preRestart hook...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable)= {
    println("Aphrodite postRestart hook...")
    super.postRestart(reason)
  }

  override def postStop() = {
    println("Aphrodite postStop...")
  }

  def receive = {
    case "Resume" =>
      throw ResumeException
    case "Stop" =>
      throw StopException
    case "Restart" =>
      println("Restart is received in Aphrodite and RestartException is thrown:")
      throw RestartException
    case _ =>
      throw new Exception
  }
}

class Hera extends Actor {
  import scala.concurrent.duration._
  var childRef: ActorRef = _

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
        case ResumeException => Resume
        case RestartException =>
          println("supervisorStrategy is invoked on RestartException")
          Restart
        case StopException => Stop
        case _: Exception => Escalate
    }

  override def preStart() = {
    println("Hera preStart hook...")
    childRef = context.actorOf(Props[Aphrodite], "Aphrodite")
    Thread.sleep(100)
  }

  def receive = {
    case msg =>
      println(s"Hera received $msg")
      childRef ! msg
      Thread.sleep(100)
  }
}


object Supervision extends App {
  val system = ActorSystem("supervision")
  val hera = system.actorOf(Props[Hera], "hera")

//  hera ! "Resume"
  hera ! "Restart"


  Thread.sleep(1000)
  println()
  system.terminate()
}
