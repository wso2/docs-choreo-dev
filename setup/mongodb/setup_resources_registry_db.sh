#!/bin/bash

show_usage_and_exit() {
   echo "Usage: $0 [-s REQUIRED_OPTION] [-u REQUIRED_OPTION] [-p REQUIRED_OPTION]" >&2
   echo "This script creates the resources collection and two indexes required for resources_registry mongo db."
   echo "Mandatory arguments:"
   echo "     -s hostname of the mongodb atlas server"
   echo "     -u username of the user"
   echo "     -p password of the user"
   exit 1
}

HOST=""
USERNAME=""
PASSWORD=""

while getopts ":s:u:p:h" FLAG; do
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

echo "Creating resources registry db's collections and indexes ..."

mongosh "$HOST/RESOURCE_REGISTRY" -u "$USERNAME" -p "$PASSWORD" scripts/resource-registry-db-resource-collection-indexes.js > /dev/null
