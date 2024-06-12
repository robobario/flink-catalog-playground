#!/bin/bash
echo "\"helloworld\",\"1\"" | kcat -b kafka:9092 -P -t mytopic
