#!/bin/bash

docker build -t choreoipaas/choreo_db:latest .
docker push choreoipaas/choreo_db:latest
