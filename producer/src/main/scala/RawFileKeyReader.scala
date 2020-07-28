import java.io.IOException
import java.nio.file.Files
import java.util

import scala.io.Source

import org.apache.pulsar.client.api.{CryptoKeyReader, EncryptionKeyInfo}

class RawFileKeyReader extends CryptoKeyReader {

  private val publicKeyFile = "testKeys/test_ecdsa_pubkey.pem"
  private val privateKeyFile = "testKeys/test_ecdsa_privkey.pem"

  override def getPublicKey(keyName: String, metadata: util.Map[String, String]): EncryptionKeyInfo = {
    val keyInfo: EncryptionKeyInfo = new EncryptionKeyInfo()

    try {
      System.out.println("Retrieving key for tenant " + keyName)
//      keyInfo.setKey(Source.fromResource(publicKeyFile).getLines().mkString.getBytes)
      keyInfo.setKey(KeyManager.keyMap.getOrElse(keyName, "Key does not exist".getBytes()))
      keyInfo.setMetadata(metadata);
    } catch {
      case e: IOException =>
        print("ERROR: Failed to read public key from file " + publicKeyFile)
        e.printStackTrace()
    }

    keyInfo
  }

  override def getPrivateKey(keyName: String, metadata: util.Map[String, String]): EncryptionKeyInfo = {
    val keyInfo = new EncryptionKeyInfo

    try {
      keyInfo.setKey(Source.fromResource(privateKeyFile).getLines().mkString.getBytes)

    } catch {
      case e: IOException =>
        print("ERROR: Failed to read private key from file " + privateKeyFile)
        e.printStackTrace()
    }

    keyInfo
  }
}
