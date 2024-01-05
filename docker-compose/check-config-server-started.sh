#!/bin/bash
# check-config-server-started.sh

#running an update first
apt-get update -y
#install the curl command line utility
yes | apt-get install curl
#running  a curl command against the config-server health endpoint, extracting http_code
curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)

echo "result status code:" "$curlResult"
#checking if we get correct response from health endpoint server in a loop, other log to console, sleep for 2s and retry the curl
while [[ ! $curlResult == "200" ]]; do
  >&2 echo "Config server is not up yet!"
  sleep 2
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)
done

#continue launching our spring boot application, using the below command, original entrypoint
./cnb/lifecycle/launcher

#make the shell script runnable : chmod +x check-config-server-started.sh