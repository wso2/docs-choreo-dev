#!/bin/bash

# shellcheck disable=SC2046
mvn clean verify -DsuiteXmlFile=src/test/resources/dp.xml -DTestConfig="$1"-env-config.yaml -DToken="$2"

