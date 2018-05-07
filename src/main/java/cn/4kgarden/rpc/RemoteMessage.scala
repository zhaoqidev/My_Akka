

class RemoteMessage extends Serializable


/**
  * worker send msg to master
  *
  * @param id
  * @param memory
  * @param cores
  */
case class RegisterWorker(id: String, memory: Int, cores: Int) extends RemoteMessage


case class HeartBeat(id: String) extends RemoteMessage


/**
  * master save master msg
  *
  * @param masterUrl
  */
case class RegisteredWorker(masterUrl: String) extends RemoteMessage


/**
  * worker beat to self
  */

case object SendHeartBeat


/**
  * master beat to self
  */
case object CheckTimeOutWorker





