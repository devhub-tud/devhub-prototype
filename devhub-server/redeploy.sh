#!/bin/bash
set -e
mvn clean install
cd web
mvn clean package -DskipTests=true -P production
cd ..
scp web/target/devhub.war devhub@dea.hartveld.com:~
ssh devhub@dea.hartveld.com bash startDevhub.sh restart
