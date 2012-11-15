#!/bin/bash
set -e
mvn clean install
scp web/target/devhub.war devhub@dea.hartveld.com:~
ssh devhub@dea.hartveld.com bash startDevhub.sh restart
