#!/bin/sh
cd src/utils/applinking_test &&
docker container run --rm -v "$(pwd)":/home/ballerina -u "$(id -u)":"$(id -g)" -e JAVA_OPTS="-Duser.home=/home/ballerina" ballerina/ballerina:swan-lake-preview8 ballerina build --observability-included &&
rm -f ballerina.conf .choreoproject &&
if [ "$CHOREO_ENV" = "DEV" ]; then
  cp ballerina.dev.template.conf ballerina.conf
elif [ "$CHOREO_ENV" = "STAGE" ]; then
  cp ballerina.stage.template.conf ballerina.conf
elif [ "$CHOREO_ENV" = "PROD" ]; then
  cp ballerina.prod.template.conf ballerina.conf
else
  echo "\$CHOREO_DEV env variable should be one of 'DEV', 'TAGE', 'PROD' !"  >&2
fi &&
java -jar target/bin/applinking_test.jar
