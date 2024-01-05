#!/bin/bash
# check-kafka-topics-created.sh
# alternative way of checking initialization logic, we already added initialization check programmatically using @EventListener

apt-get update -y

yes | apt-get install kafkacat

kafkacatResult=$(kafkacat -L -b kafka-broker-1:9092) # to list the topics

echo "kafkacat result:" $kafkacatResult

while [[ ! $kafkacatResult == *"twitter-topic"* ]]; do
  >&2 echo "Kafka topic has not been created yet!"
  sleep 2
  kafkacatResult=$(kafkacat -L -b kafka-broker-1:9092)
done

#continue launching our spring boot application, using the below command, original entrypoint
./cnb/lifecycle/launcher

#make the shell script runnable : chmod +x check-kafka-topics-created.sh