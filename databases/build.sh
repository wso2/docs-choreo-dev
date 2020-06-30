#!/bin/bash

docker build -t choreoipaas/choreo_db:0.2.6 .
docker push choreoipaas/choreo_db:0.2.6
