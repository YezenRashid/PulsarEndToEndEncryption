import java.io.IOException
import java.util

import scala.io.Source

import org.apache.pulsar.client.api.{CryptoKeyReader, EncryptionKeyInfo}

class RawFileKeyReader extends CryptoKeyReader {

  private val publicKeyFile = "testKeys/test_ecdsa_pubkey.pem"

  override def getPublicKey(keyName: String, metadata: util.Map[String, String]): EncryptionKeyInfo = {
    val keyInfo: EncryptionKeyInfo = new EncryptionKeyInfo()

    try {
      keyInfo.setKey(Source.fromResource(publicKeyFile).getLines().mkString.getBytes)
    } catch {
      case e: IOException =>
        print("ERROR: Failed to read public key from file " + publicKeyFile)
        e.printStackTrace()
    }

    keyInfo
  }

  override def getPrivateKey(keyName: String, metadata: util.Map[String, String]): EncryptionKeyInfo = {
    val keyInfo = new EncryptionKeyInfo
    System.out.println("keyname = " + keyName)

    try {
      // Change this to just "tenant1" to show an example of how this will only be able to decrypt messages related to tenant1.
      keyInfo.setKey(KeyManager.keyMap.getOrElse(keyName, "trash".getBytes()))
    } catch {
      case e: IOException =>
        print("ERROR: Failed to set private key for tenant " + keyName)
        e.printStackTrace()
    }

    keyInfo
  }
}
