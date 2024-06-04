#!/bin/bash

show_usage_and_exit() {
   echo "Usage: $0 [-b REQUIRED_OPTION] [-v REQUIRED_OPTION] [-i REQUIRED_OPTION] [-r REQUIRED_OPTION] [-e REQUIRED_OPTION]" >&2
   echo "This script grants given role to a given email user for a particular MongoDB project."
   echo "Mandatory arguments:"
   echo "     -b    public key of the admin api"
   echo "     -v    private key of the admin api"
   echo "     -i    id of the mongodb atlas project"
   echo "     -r    project level role to be granted"
   echo "     -e    email id to which access granted"
   exit 1
}

remove_quotes() {
  echo "$1" | tr -d '"'
}

API_PUBLIC_KEY=""
API_PRIVATE_KEY=""
ROLE=""
EMAIL_ID=""
PROJECT_ID=""

while getopts ":b:v:i:r:e:h" FLAG; do
    case $FLAG in
        b)
            # -b public key of the admin api
            API_PUBLIC_KEY=$OPTARG
            ;;
        v)
            # -v private key of the admin api
            API_PRIVATE_KEY=$OPTARG
            ;;
	      i)
            # -i id of the mongodb atlas project
            PROJECT_ID=$OPTARG
            ;;
        r)
            # -r project level role to be granted
            ROLE=$OPTARG
            ;;
        e)
            # -e email id to which access granted
            EMAIL_ID=$OPTARG
            ;;
	      h)
            show_usage_and_exit
            ;;
	      \?)
            # Invalid option
            echo "Invalid option: -$OPTARG" >&2
            exit 1
            ;;
        :)
            # Missing argument for an option that requires one
            echo "Option -$OPTARG requires an argument" >&2
            exit 1
            ;;
    esac
done

echo "Granting $ROLE access to $EMAIL_ID for MongoDB Project $PROJECT_ID ..."

curl -s -X POST -u "$API_PUBLIC_KEY:$API_PRIVATE_KEY" --digest \
        --header "Accept: application/vnd.atlas.2023-02-01+json" \
        --header "Content-Type: application/json" \
        --data "$(jq -n \
            --arg email_id "$(remove_quotes "$EMAIL_ID")" \
            --arg role "$(remove_quotes "$ROLE")" '{
              "roles": [
                          $role
                        ],
              "username": $email_id
            }')" \
        "https://cloud.mongodb.com/api/atlas/v2/groups/$PROJECT_ID/access"
