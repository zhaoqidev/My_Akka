import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


class Worker(val masterHost: String, val masterPort: String, val memory: Int, val cores: Int) extends Actor {

  var master: ActorSelection = _

  var workerId = UUID.randomUUID().toString

  val HEART_INTERVAL = 10000

  override def preStart(): Unit = {

    //connect to master
    master = context.actorSelection(s"akka.tcp://MasterSystem@$masterHost:$masterPort(user/Master)")
    master ! RegisterWorker(workerId, memory, cores)

  }


  override def receive: Receive = {

    case RegisteredWorker(masterUrl) => {

      context.system.scheduler.schedule(0 millis, HEART_INTERVAL millis, self, SendHeartBeat)
    }

    case SendHeartBeat => {
      master ! HeartBeat(workerId)
    }


  }
}

object Worker {


  def main(args: Array[String]): Unit = {


    val host = args(0)
    val port = args(1).toInt
    val masterHost = args(2)
    val masterPort = args(3)
    val memory = args(4).toInt
    val cores = args(5).toInt


    val config =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname="$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin

    val conf = ConfigFactory.parseString(config)

    val actorSystem = ActorSystem("WorderSystem", conf)


    val master = actorSystem.actorOf(Props(new Worker(masterHost, masterPort, memory, cores)), "Master")

    actorSystem.awaitTermination()

  }


}


