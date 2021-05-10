#!/bin/sh
cd cypress/applinking-configs &&
docker container run --rm -v "$(pwd)":/home/ballerina -u "$(id -u)":"$(id -g)" -e JAVA_OPTS="-Duser.home=/home/ballerina" ballerina/ballerina:swan-lake-alpha2 bal build --observability-included &&
rm -f Config.toml .choreoproject &&
sed 's|<REPORTER_HOST_NAME>|'"$REPORTER_HOST_NAME"'|' Config.template.toml > Config.toml &&
java -jar target/bin/applinking_test-0.1.0.jar