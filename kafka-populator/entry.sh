#!/bin/sh

echo "Waiting for kafka to be connectable on 9092"

while ! nc -z kafka 9092; do   
  echo "kafka not ready on kafka:9092, sleeping 1 second"
  sleep 1
done

exec "/populate.sh"
