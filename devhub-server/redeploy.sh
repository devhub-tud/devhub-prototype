#!/bin/bash
set -e
mvn clean install
cd web
mvn clean package -DskipTests=true -P production
cd ..
scp web/target/devhub.war devhub@devhub.nl:~
ssh devhub@devhub.nl sh /etc/init.d/devhub restart
