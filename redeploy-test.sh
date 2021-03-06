#!/bin/bash

set -e

USER="devhub"
HOST="dev.devhub.nl"

mvn clean install
cd devhub-server/web
mvn clean package -DskipTests=true -P dev
cd ../..
scp devhub-server/web/target/devhub.war $USER@$HOST:~
scp $USER@$HOST:~/jetty.out .
scp jetty.out $USER@$HOST:~/jetty.out.previous-version
rm -f jetty.out
ssh $USER@$HOST sh /home/$USER/startDevhub.sh restart
