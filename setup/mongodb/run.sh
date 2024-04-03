#!/bin/bash

show_usage_and_exit() {
   echo "Usage: $0 [-s REQUIRED_OPTION] [-u REQUIRED_OPTION] [-p REQUIRED_OPTION] [-b REQUIRED_OPTION] [-v REQUIRED_OPTION] [-g REQUIRED_OPTION] [-c REQUIRED_OPTION]" >&2
   echo "This script setups the required collections and indexes in apim db and resources_registry db in mongodb cluster."
   echo "Mandatory arguments:"
   echo "     -s    hostname of the mongodb atlas server"
   echo "     -u    username of the user"
   echo "     -p    password of the user"
   echo "     -b    public key of the admin api"
   echo "     -v    private key of the admin api"
   echo "     -g    id of the mongodb atlas project"
   echo "     -c    name of the mongodb atlas cluster"
   exit 1
}

HOST=""
USERNAME=""
PASSWORD=""
API_PUBLIC_KEY=""
API_PRIVATE_KEY=""
PROJECT_ID=""
CLUSTER=""

while getopts ":s:u:p:b:v:g:c:h" FLAG; do
    case $FLAG in
        s)
            # -s hostname of the mongodb atlas server
            HOST=$OPTARG
            ;;
        u)
            # -u username of the user
            USERNAME=$OPTARG
            ;;
	p)
            # -p password of the user
            PASSWORD=$OPTARG
            ;;
	b)
            # -pubk public key of the admin api
            API_PUBLIC_KEY=$OPTARG
            ;;
	v)
            # -pvtk private key of the admin api
            API_PRIVATE_KEY=$OPTARG
            ;;
	g)
            # -g id of the mongodb atlas project
            PROJECT_ID=$OPTARG
            ;;
	c)
            # -c name of the mongodb atlas cluster
            CLUSTER=$OPTARG
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

echo "Setting up apim db .."
bash setup_apim_db.sh -s "$HOST" -u "$USERNAME" -p "$PASSWORD" -b "$API_PUBLIC_KEY" -v "$API_PRIVATE_KEY" -g "$PROJECT_ID" -c "$CLUSTER"

echo "Setting up resources_registry db .."
bash setup_resources_registry_db.sh -s "$HOST" -u "$USERNAME" -p "$PASSWORD"
