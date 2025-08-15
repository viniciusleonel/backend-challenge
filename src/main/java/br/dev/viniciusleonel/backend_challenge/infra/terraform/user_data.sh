#!/bin/bash

sudo su
yum update -y
yum install -y docker
service docker start
usermod -a -G docker ec2-user

docker pull viniciusleonel/backend-challenge:latest
docker stop backend || true
docker rm backend || true
docker run -d --name backend -p 80:8080 viniciusleonel/backend-challenge:latest