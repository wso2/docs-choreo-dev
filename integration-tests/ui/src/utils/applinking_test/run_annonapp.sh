#!/bin/sh
cd src/utils/applinking_test &&
docker container run --rm -v "$(pwd)":/home/ballerina -u "$(id -u)":"$(id -g)" -e JAVA_OPTS="-Duser.home=/home/ballerina" ballerina/ballerina:swan-lake-alpha2 bal build --observability-included &&
rm -f Config.toml .choreoproject &&
if [ "$CHOREO_ENV" = "DEV" ]; then
  cp Config.dev.template.toml Config.toml
elif [ "$CHOREO_ENV" = "STAGE" ]; then
  cp Config.stage.template.toml Config.toml
elif [ "$CHOREO_ENV" = "PROD" ]; then
  cp Config.prod.template.toml Config.toml
else
  echo "\$CHOREO_ENV env variable should be one of 'DEV', 'STAGE', 'PROD' !"  >&2
fi &&
java -jar target/bin/applinking_test-0.1.0.jar
