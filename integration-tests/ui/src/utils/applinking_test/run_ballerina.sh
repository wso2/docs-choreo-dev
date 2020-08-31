#!/bin/sh
cd src/utils/applinking_test &&
docker container run --rm -v "$(pwd)":/home/ballerina -u "$(id -u)":"$(id -g)" -e JAVA_OPTS="-Duser.home=/home/ballerina" ballerina/ballerina:swan-lake-preview3 ballerina build --observability-included test &&
rm -f ballerina.conf .choreoproject &&
if [ "$CHOREO_ENV" = "dev" ]; then   
    cp ballerina.dev.template.conf ballerina.conf
elif [ "$CHOREO_ENV" = "stage" ]; then
    cp ballerina.stage.template.conf ballerina.conf
elif [ "$CHOREO_ENV" = "prod" ]; then
    cp ballerina.prod.template.conf ballerina.conf
fi &&
echo "[b7a.observability.choreo.application]" >> ballerina.conf && 
echo "$1" >> ballerina.conf && 
java -jar target/bin/test.jar
