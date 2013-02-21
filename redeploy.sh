#!/bin/bash

set -e 

USER="devhub"
HOST="devhub.nl"

mvn clean install
cd devhub-server/web
mvn clean package -DskipTests=true -P production
cd ../..
scp devhub-server/web/target/devhub.war $USER@$HOST:~
scp $USER@$HOST ~/jetty.out .
scp jetty.out ~/jetty.out.previous-version
rm -f jetty.out
ssh $USER@$HOST sh /etc/init.d/devhub restart
