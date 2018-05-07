import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


class Master(val host: String, val port: Int) extends Actor {

  println("construct running")


  override def preStart(): Unit = {
    println("preStart")
  }


  override def receive: Receive = {

    case "connect" => {
      println("connect ")
      sender ! " receive replay"
    }

    case "hello" => {

      println("hello")
    }

  }

}


object Master {

  def main(args: Array[String]): Unit = {

    val host = args(0)
    val port = args(1).toInt
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname="$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin


    val config = ConfigFactory.parseString(configStr)
    val actorSystem = ActorSystem("MasterSystem", config)
    val master = actorSystem.actorOf(Props[Master], "Master")
    master ! "hello i'm master"
    actorSystem.awaitTermination()
  }


}