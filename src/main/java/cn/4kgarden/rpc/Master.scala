import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._

class Master(val host: String, val port: Int) extends Actor {

  //保存心跳信息集合
  val idToWorker = new mutable.HashMap[String, WorkerInfo]()

  val workers = new mutable.HashSet[WorkerInfo]()


  val CHECK_INTERVAL = 12000


  override def preStart(): Unit = {

    println("prestart invoked")
    import context.dispatcher
    context.system.scheduler.schedule(0 millis, CHECK_INTERVAL millis, self, CheckTimeOutWorker)

  }


  override def receive: Receive = {
    case RegisterWorker(id, memory, cores) => {
      if (!idToWorker.contains(id)) {
        val workerInfo = new WorkerInfo(id, memory, cores)

        idToWorker(id) = workerInfo

        workers += workerInfo

        sender ! RegisteredWorker(s"akka.tcp://MasterSystem@$host:$port/user/Master")
      }

    }

    case HeartBeat(workerId) => {
      //master save last work  beat time

      if (idToWorker.contains(workerId)) {
        val workerInfo = idToWorker(workerId)
        val currentTime = System.currentTimeMillis()
        workerInfo.lastHeartBeatsTime = currentTime
      }
    }


    case CheckTimeOutWorker => {

      val currentTime = System.currentTimeMillis()
      val willRemove = workers.filter(x => currentTime - x.lastHeartBeatsTime > CHECK_INTERVAL)


      //time out remove worker
      for (w <- willRemove) {
        workers -= w
        idToWorker -= w.id
      }
    }

  }
}


object Master {

  def main(args: Array[String]): Unit = {


    val host = args(0)
    val port = args(1).toInt

    val config =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname="$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin

    val conf = ConfigFactory.parseString(config)

    val actorSystem = ActorSystem("MasterSystem", conf)


    val master = actorSystem.actorOf(Props(new Master(host, port)), "Master")

    actorSystem.awaitTermination()

  }


}
