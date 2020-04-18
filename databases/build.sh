#!/bin/bash

for db in $(ls -1 scripts | sed -e 's/\..*$//'); do
    docker build --build-arg DATABASE=${db} -t choreoipaas/${db}:latest .
    docker push choreoipaas/${db}:latest
done
