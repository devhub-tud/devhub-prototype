#!/bin/bash

USER="devhub"
HOST="dev.devhub.nl"

set -e
mvn clean install
cd web
mvn clean package -DskipTests=true -P dev
cd ..
scp web/target/devhub.war $USER@$HOST:~
ssh $USER@$HOST sh /home/$USER/startDevhub.sh restart
