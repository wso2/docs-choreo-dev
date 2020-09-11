#!/bin/bash

docker build -t choreoipaas/choreo_db:0.2.9 .
docker push choreoipaas/choreo_db:0.2.9
