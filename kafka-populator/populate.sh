#!/bin/sh
echo "sending message into mytopic"
echo "\"helloworld\",\"1\"" | kcat -b kafka:9092 -P -t mytopic
echo "sent message into mytopic"
