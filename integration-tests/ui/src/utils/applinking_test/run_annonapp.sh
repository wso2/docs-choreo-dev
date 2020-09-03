#!/bin/sh
export CHOREO_ENV="DEV" &&
cd src/utils/applinking_test &&
docker container run --rm -v "$(pwd)":/home/ballerina -u "$(id -u)":"$(id -g)" -e JAVA_OPTS="-Duser.home=/home/ballerina" ballerina/ballerina:swan-lake-preview3 ballerina build --observability-included test &&
rm -f ballerina.conf .choreoproject &&
if [ "$CHOREO_ENV" = "DEV" ]; then   
    cp ballerina.dev.template.conf ballerina.conf
elif [ "$CHOREO_ENV" = "STAGE" ]; then
    cp ballerina.stage.template.conf ballerina.conf
elif [ "$CHOREO_ENV" = "PROD" ]; then
    cp ballerina.prod.template.conf ballerina.conf
fi &&
java -jar target/bin/test.jar
