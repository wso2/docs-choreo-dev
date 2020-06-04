#!/bin/bash

docker build -t choreoipaas/choreo_db:0.2.4 .
docker push choreoipaas/choreo_db:0.2.4
