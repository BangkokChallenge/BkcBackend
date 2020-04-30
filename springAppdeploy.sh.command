#!/bin/bash
echo "***** 배포를 시작합니다 *****"
echo "***** MAVEN PACKAGE 시작 *****"
mvn package
echo "***** DOCKER BUILD 시작 *****"
mvn package docker:build
echo "***** DOCKER LOGIN *****"
docker login
mvn docker:push
echo "*****EC2 UPLOAD 완료 *****"
