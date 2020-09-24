#!/bin/bash

docker build -t choreoipaas/choreo_db:0.3.0 .
docker push choreoipaas/choreo_db:0.3.0
