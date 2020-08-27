#!/bin/sh
cd src/utils/applinking_test &&
docker container run --rm -it -v $(pwd):/home/ballerina -u $(id -u):$(id -g) -e JAVA_OPTS="-Duser.home=/home/ballerina" ballerina/ballerina:swan-lake-preview3 ballerina build --observability-included test &&
rm -f ballerina.conf && 
cp ballerina.template.conf ballerina.conf && 
echo "[b7a.observability.choreo.application]" >> ballerina.conf && 
echo "$1" >> ballerina.conf && 
java -jar target/bin/test.jar
