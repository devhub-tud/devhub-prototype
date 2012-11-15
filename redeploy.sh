#!/bin/bash
set -e
mvn clean install -P production
scp web/target/devhub.war devhub@dea.hartveld.com:~
ssh devhub@dea.hartveld.com bash startDevhub.sh restart
