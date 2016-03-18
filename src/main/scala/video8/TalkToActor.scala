package video8

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import video8.Checker.{BlackUser, CheckUser, WhiteUser}
import video8.Recorder.NewUser
import video8.Storage.AddUser

import scala.concurrent.duration._

/**
  * Created by mtulaza on 2016-03-18.
  */
case class User(username: String, email: String)

object Recorder {
  sealed trait RecorderMsg
  case class NewUser(user: User) extends RecorderMsg

  def props(checker: ActorRef, storage: ActorRef) =
    Props(new Recorder(checker, storage))
}

object Checker {
  sealed trait CheckerMsg
  case class CheckUser(user: User) extends CheckerMsg

  sealed trait CheckerResponse
  case class BlackUser(user: User) extends CheckerResponse
  case class WhiteUser(user: User) extends CheckerResponse
}

object Storage {
  sealed trait StorageMsg
  case class AddUser(user: User) extends StorageMsg
}

class Checker extends Actor {
  val blackList = List(
    User("Adam", "adam@mail.com")
  )
  def receive = {
    case CheckUser(user) if blackList.contains(user) =>
      println(s"Checker: $user in the blacklist")
      sender() ! BlackUser(user)
    case CheckUser(user) =>
      println(s"Checker: $user not in the blacklist")
      sender() ! WhiteUser(user)
  }
}

class Storage extends Actor {
  var users = List.empty[User]

  def receive = {
    case AddUser(user) =>
      println(s"Storage: $user added")
      users = user :: users
  }
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case NewUser(user) =>
      checker ? CheckUser(user) map {
        case WhiteUser(user) =>
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"Recorder: $user in the blacklist")
      }
  }

}

object TalkToActor extends App {

  val system = ActorSystem("talk-to-actor")

  val checker = system.actorOf(Props[Checker], "checker")
  val storage = system.actorOf(Props[Storage], "storage")
  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  recorder ! NewUser(User("Jon", "jon@packt.com"))
  recorder ! NewUser(User("Adam", "adam@mail.com"))

  Thread.sleep(100)
  system.terminate
}
