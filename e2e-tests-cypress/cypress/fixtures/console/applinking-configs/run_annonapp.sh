#!/bin/sh
cd cypress/fixtures/console/applinking-configs &&
rm -f Config.toml .choreoproject Dependencies.toml &&
sed 's|<REPORTER_HOST_NAME>|'"$REPORTER_HOST_NAME"'|' Config.template.toml > Config.toml &&
java -jar applinking_test.jar
