#!/bin/bash

# shellcheck disable=SC2046
mvn clean verify -DsuiteXmlFile=src/test/resources/security.xml -DTestConfig="$1"-env-config.yaml -DToken="$2"

