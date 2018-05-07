


/**
  * 心跳传递的信息
  *
  * @param id
  * @param memory
  * @param cores
  */
class WorkerInfo(val id: String, val memory: Int, val cores: Int) {
  var lastHeartBeatsTime: Long = _
}
