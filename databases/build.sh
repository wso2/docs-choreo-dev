#!/bin/bash

docker build -t choreoipaas/choreo_db:0.5.0 .
docker push choreoipaas/choreo_db:0.5.0
