#!/bin/bash

docker build -t choreoipaas/choreo_db:0.1.0 .
docker push choreoipaas/choreo_db:0.1.0
