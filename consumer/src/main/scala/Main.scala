import org.apache.pulsar.client.api.{Consumer, Message, PulsarClient}

object Main {
  def main(args: Array[String]): Unit = {
    val pulsarClient = PulsarClient.builder()
      .serviceUrl("pulsar://localhost:6650")
      .build()

    val rawFileKeyReader: RawFileKeyReader = new RawFileKeyReader

    val consumer: Consumer[Array[Byte]] = pulsarClient.newConsumer
      .topic("pulsarEncryptedData")
      .subscriptionName("jvmConsumer")
      .cryptoKeyReader(rawFileKeyReader)
      .subscribe()

    System.out.println("I am going to recieve messages.")
    while(true) {
      val msg: Message[Array[Byte]] = consumer.receive
      System.out.println("Received: " + new String(msg.getData))
      consumer.acknowledge(msg)
    }

    consumer.close()
  }
}
