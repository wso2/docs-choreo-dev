#!/bin/bash

for db in "choreo_app_db" "choreo_perf_db" "choreo_program_db" "choreo_trace_db"; do
    docker build --build-arg DATABASE=${db} -t choreoipaas/${db}:latest .
    docker push choreoipaas/${db}:latest
done
