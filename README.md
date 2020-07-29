# End to End Encryption Using Pulsar

# Requirements
Must have a pulsar container or instance running.

# Summary
In this repo you will find examples of how to send encrypted messages over pulsar.

Here are examples using pulsar.

There is an example of a producer sending messages using pulsar with pulsar's encryption and it's own encryption of messages.

I have set up two consumers.  One in scala (JVM language) and one in Go using Pulsar's native GO client. 

Pulsar's native GO client does not currently support message encryption and decryption.  So on the producer side it can use its own custom encryption and send it using pulsar and the go client consumer can then read in the message and decrypt it itself using the appropriate private key.

For the JVM consumer it uses pulsars built in message encryption and can decrypt messages per tenant depending on the messages metadata.
