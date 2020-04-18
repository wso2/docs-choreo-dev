#!/bin/bash

for db in $(ls scripts | sed 's/\(.*\)\..*/\1/'); do
    docker build --build-arg DATABASE=${db} -t choreoipaas/${db}:latest .
    docker push choreoipaas/${db}:latest
done
