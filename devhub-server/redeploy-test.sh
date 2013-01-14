#!/bin/bash

USER="devhub"
HOST="devhub.hartveld.com"

set -e
mvn clean install
cd web
mvn clean package -DskipTests=true -P production
cd ..
scp web/target/devhub.war $USER@$HOST:~
ssh $USER@$HOST sh /home/$USER/startDevhub.sh restart
