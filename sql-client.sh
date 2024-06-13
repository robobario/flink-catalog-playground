#!/bin/bash
mvn clean verify
cp target/flink-catalog-playground-1.0-SNAPSHOT.jar ./custom-flink/
docker compose down
docker compose build
docker compose run sql-client
