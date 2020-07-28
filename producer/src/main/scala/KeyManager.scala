import java.io.InputStream

object KeyManager {
  val testKeyFolder: String = "testKeys/"
  val tenant1PubKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "tenant1_pubkey.pem")
  val tenant2PubKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "tenant2_pubkey.pem")
  val tenant3PubKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "tenant3_pubkey.pem")
  val rsaPubKey: Array[Byte] = getFileAsByteArray(testKeyFolder + "rsa_public.crt")

  val keyMap: Map[String, Array[Byte]] = Map("tenant1" -> tenant1PubKey, "tenant2" -> tenant2PubKey, "tenant3" -> tenant3PubKey)

  def getFileAsByteArray(filePath: String): Array[Byte] = {
    val stream: InputStream = getClass.getResourceAsStream(filePath)

    LazyList.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray
  }
}
