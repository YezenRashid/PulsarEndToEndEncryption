import java.io.InputStream

object KeyManager {
  val testKeyFolder: String = "testKeys/"
  val tenant1PrivKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "tenant1_privkey.pem")
  val tenant2PrivKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "tenant2_privkey.pem")
  val tenant3PrivKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "tenant3_privkey.pem")

  val keyMap: Map[String, Array[Byte]] = Map("tenant1" -> tenant1PrivKey, "tenant2" -> tenant2PrivKey, "tenant3" -> tenant3PrivKey)

  def getFileAsByteArray(filePath: String): Array[Byte] = {
    val stream: InputStream = getClass.getResourceAsStream(filePath)

    LazyList.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray
  }

}
