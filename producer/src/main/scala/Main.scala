
import java.io.StringReader
import java.nio.file.{Files, Paths}
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.security.{KeyFactory, PrivateKey, PublicKey}
import java.util.UUID

import javax.crypto.Cipher

import org.apache.pulsar.client.api.{Producer, PulsarClient}
import org.bouncycastle.util.io.pem.PemReader

import sun.misc.BASE64Encoder

object Main {
  def main(args: Array[String]): Unit = {
    val client: PulsarClient = PulsarClient.builder()
      .serviceUrl("pulsar://localhost:6650")
      .build()

    sendAllMessagesWithPulsarEncryption(client)
    sendAllMessagesWithCustomEncryption(client)

    client.close()
  }

  // Shows an example of using Pulsar's built in encryption to send encrypted messages over pulsar.  You can use their interface to
  // encrypt the data using different tenant public keys.  It would be up to us to pull the correct public key for the correct tenant.
  // This method shows an example of sending 3 different public keys belonging to 3 different tenants and 10 messages being encrypted for that
  // same tenant.  Topics can have multiple messages encrypted in different ways.  Each pulsar message has
  // its own metadata attached.
  private def sendAllMessagesWithPulsarEncryption(client: PulsarClient): Unit = {
    val rawFileKeyReader: RawFileKeyReader = new RawFileKeyReader
    for ((tenant, _) <- KeyManager.keyMap) {
      produceMessagesForTenantPulsarEncryption(tenant, client, rawFileKeyReader)
    }
  }

  private def produceMessagesForTenantPulsarEncryption(tenant: String, client: PulsarClient, rawFileKeyReader: RawFileKeyReader): Unit = {
    val producer: Producer[Array[Byte]] = client.newProducer()
      .topic("pulsarEncryptedData")
      .addEncryptionKey(tenant)
      .cryptoKeyReader(rawFileKeyReader)
      .create()

    for (_ <- 1 to 10) {
      System.out.println("Sending message")
      val message: String = s"${tenant} user with ${UUID.randomUUID().toString} has been updated."
      producer.send(message.getBytes())
    }

    producer.close()
  }

  // This is an example of using pulsar to send our own encrypted messages.  I take in a public key
  // and encrypt the message using that public key.  This can be extended to be used for multiple tenants.
  // A go client will be able to ingest these messages provided they have the corresponding private key.
  private def sendAllMessagesWithCustomEncryption(client: PulsarClient): Unit = {
    val producer: Producer[Array[Byte]] = client.newProducer()
      .topic("customEncryptedData")
      .create()

    val pubKey = readPublicKey(KeyManager.rsaPubKey.map(_.toChar).mkString)
    for (_ <- 1 to 10) {
      System.out.println("Sending message")
      val message: String = s"Custom Encryption of user with ${UUID.randomUUID().toString} has been updated."
      val encryptedMessage: Array[Byte] = new BASE64Encoder().encode(encrypt(pubKey, message)).getBytes()
      producer.send(encryptedMessage)
    }

    producer.close()
  }

  def readFileBytes(filename: String): Array[Byte] = {
    val path = Paths.get(filename)
    Files.readAllBytes(path)
  }

  def readPublicKey(publicKeyString: String): PublicKey = {
    val pem = new PemReader(new StringReader(publicKeyString)).readPemObject
    val pubKeyBytes = pem.getContent
    val publicSpec = new X509EncodedKeySpec(pubKeyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    keyFactory.generatePublic(publicSpec)
  }

  def encrypt(key: PublicKey, plaintext: String): Array[Byte] = {
    val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(plaintext.getBytes("UTF-8"))
  }
}
