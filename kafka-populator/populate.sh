#!/bin/sh
echo "sending message into mytopic"
echo "\"helloworld\",\"1\"" | kcat -b kafka:9092 -P -t mytopic
echo "\"helloerin\",\"1\"" | kcat -b kafka:9092 -P -t erintopic
echo "\"helloeric\",\"1\"" | kcat -b kafka:9092 -P -t erictopic
echo "\"helloSamwise\",\"1\"" | kcat -b kafka:9092 -P -t samwisetopic
echo "sent message into mytopic"
