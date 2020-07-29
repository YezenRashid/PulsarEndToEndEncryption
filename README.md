# End to End Encryption Using Pulsar

# Requirements
Must have a pulsar container or instance running.

Create your ECDSA or RSA public/private key pair for the tenant keys following

```
openssl ecparam -name secp521r1 -genkey -param_enc explicit -out test_ecdsa_privkey.pem
openssl ec -in test_ecdsa_privkey.pem -pubout -outform pkcs8 -out test_ecdsa_pubkey.pem
```

For generating the RSA key to be used for the go client decryption you can follow
```
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out rsa_public.crt
```

# Summary
In this repo you will find examples of how to send encrypted messages over pulsar.

Here are examples using pulsar.

There is an example of a producer sending messages using pulsar with pulsar's encryption and it's own encryption of messages.

I have set up two consumers.  One in scala (JVM language) and one in Go using Pulsar's native GO client. 

Pulsar's native GO client does not currently support message encryption and decryption.  So on the producer side it can use its own custom encryption and send it using pulsar and the go client consumer can then read in the message and decrypt it itself using the appropriate private key.

For the JVM consumer it uses pulsars built in message encryption and can decrypt messages per tenant depending on the messages metadata.
