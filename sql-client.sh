#!/bin/bash

docker compose down
docker compose build
docker compose run sql-client
