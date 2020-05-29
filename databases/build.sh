#!/bin/bash

docker build -t choreoipaas/choreo_db:0.2.2 .
docker push choreoipaas/choreo_db:0.2.2
