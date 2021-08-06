#!/bin/bash

print_usage(){
  echo "Usage: sh run.sh -e dev|stage|prod"
  exit
}

while getopts e: flag
do
    case "${flag}" in
        e) env=${OPTARG};;
        *) print_usage
    esac
done

if [ "$env" == "dev" ] || [ "$env" == "stage" ] || [ "$env" == "prod" ]; then
  sh register-apim-service.sh
  bash create-idp-sp.sh -e "${env}"
else
  echo "Please provide a correct environment name."
  print_usage
fi
