#!/bin/env python3
import json
import urllib.request
import sys
import time
from urllib.error import HTTPError

port=sys.argv[1]
gateway=f'http://localhost:{port}'

def send(endpoint, data):
  data = json.dumps(data).encode('utf-8')
  response = urllib.request.urlopen(gateway + endpoint, data)
  return json.load(response)

def get(endpoint):
  response = urllib.request.urlopen(gateway + endpoint)
  return json.load(response)
  
def get_status(sessionHandle, operationHandle):
  response = get(f'/sessions/{sessionHandle}/operations/{operationHandle}/status')
  return response["status"]

def get_results(sessionHandle, operationHandle):
  response = get(f'/sessions/{sessionHandle}/operations/{operationHandle}/result/0?rowFormat=JSON')
  return response

create_session={"properties": {"execution.runtime-mode": "streaming"}}
response = send("/sessions", create_session)
sessionHandle = response["sessionHandle"]

describe_session=get(f'/sessions/{sessionHandle}')
print(describe_session)

query={"statement": sys.argv[2]}
response = send(f'/sessions/{sessionHandle}/statements', query)
operationHandle = response["operationHandle"]

while get_status(sessionHandle, operationHandle) not in ["RUNNING", "FINISHED", "ERROR"]:
  print("waiting for query to run")
  time.sleep(1)

while True:
  status = get_status(sessionHandle, operationHandle)
  print(status)
  if status == "ERROR":
    print("query resulted in error")
    try:
      print(get_results(sessionHandle, operationHandle))
    except HTTPError as e:
      print(e.read())
    break
  print(get_results(sessionHandle, operationHandle))
  if status == "FINISHED":
    break
  else:
    time.sleep(2)

req = urllib.request.Request(f'{gateway}/sessions/{sessionHandle}')
req.get_method = lambda: "DELETE"
response = urllib.request.urlopen(req)
print(response.read())
