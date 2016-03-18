package video7

import akka.actor.{ActorSystem, Props, Actor}
import video7.MusicController.{Stop, Play}
import video7.MusicPlayer.{StartMusic, StopMusic}

/**
  * Created by mtulaza on 2016-03-18.
  */

object MusicController {
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg

  def props = Props[MusicController]
}

class MusicController extends Actor {
  def receive = {
    case Play =>
      println("Music Started...")
    case Stop =>
      println("Music Stopped...")
  }
}



object MusicPlayer {
  sealed trait PlayMsg
  case object StartMusic extends PlayMsg
  case object StopMusic extends PlayMsg
}

class MusicPlayer extends Actor {
  def receive = {
    case StopMusic =>
      println("I dont want to stop music")
    case StartMusic =>
      val controller = context.actorOf(MusicController.props, "controller")
      controller ! Play
    case _ =>
      println("Unknow Message")
  }
}



object ActorCreation extends App {
  val system = ActorSystem("creation")

  val player = system.actorOf(Props[MusicPlayer], "player")

  player ! StartMusic

  system.shutdown
}
