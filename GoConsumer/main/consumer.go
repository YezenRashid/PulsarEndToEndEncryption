package main

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha1"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"io/ioutil"
	"log"
	"os"
)
import "github.com/apache/pulsar-client-go/pulsar"

func main() {
	fmt.Println("Starting up the GO consumer")

	client, err := pulsar.NewClient(pulsar.ClientOptions{
		URL: "pulsar://localhost:6650",
	})

	if err != nil {
		log.Fatal(err)
	}

	defer client.Close()
	
	consumer, err := client.Subscribe(pulsar.ConsumerOptions{
		Topic:            "customEncryptedData",
		SubscriptionName: "nativeGoConsumer",
		Type:             pulsar.Shared,
	})

	msgChannel := consumer.Chan()

	if err != nil {
		log.Fatal(err)
	}

	defer consumer.Close()

	fmt.Println("Listening in for messages")
	for cm := range msgChannel {
		msg := cm.Message

		//fmt.Printf("Message ID: %s", msg.ID())
		result, err := decrypt(msg.Payload())
		if err != nil {
			log.Fatal(err)
		}

		fmt.Printf("Message value: %s", string(result))
		//msg.Payload()
		fmt.Println()
		consumer.Ack(msg)
	}
}

func decrypt(encryptedMessage []byte) ([]byte, error) {
	msg, err := base64.StdEncoding.DecodeString(string(encryptedMessage))
	if err != nil {
		return nil, err
	}

	// Read the private key
	pwd, _ := os.Getwd()
	privKey, err := ioutil.ReadFile(pwd + "/main/keypair.pem")
	if err != nil {
		log.Fatalf("read key file: %s", err)
	}

	privateKeyBlock, _ := pem.Decode(privKey)
	var pri *rsa.PrivateKey
	pri, parseErr := x509.ParsePKCS1PrivateKey(privateKeyBlock.Bytes)
	if parseErr != nil {
		return nil, parseErr
	}

	decryptedData, err := rsa.DecryptOAEP(sha1.New(), rand.Reader, pri, msg, nil)
	if err != nil {
		return nil, err
	}
	return decryptedData, nil
}
