#!/bin/bash
set -e
mvn clean install
cd web
mvn clean package -DskipTests=true -P production
cd ..
scp web/target/devhub.war devhub@devhub.nl:~
ssh devhub@devhub.nl bash /etc/init.d/devhub restart
