#!/bin/bash

show_usage_and_exit() {
   echo "Usage: $0 [-s REQUIRED_OPTION] [-u REQUIRED_OPTION] [-p REQUIRED_OPTION] [-b REQUIRED_OPTION] [-v REQUIRED_OPTION] [-g REQUIRED_OPTION] [-c REQUIRED_OPTION]" >&2
   echo "This script creates the api collection and search index required for apim mongo db."
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

echo "Creating apim db's collections and search indexes ..."

#Create the apis collections in apim db
mongosh "$HOST/APIM_DB" -u "$USERNAME" -p "$PASSWORD" scripts/apim-db-apis-collection.js > /dev/null

#Check the existence of search index
is_search_index_exist=$(curl -s -X GET -u "$API_PUBLIC_KEY:$API_PRIVATE_KEY" --digest \
   --header "Accept: application/vnd.atlas.2023-11-15+json" \
"https://cloud.mongodb.com/api/atlas/v2/groups/$PROJECT_ID/clusters/$CLUSTER/fts/indexes/APIM_DB/apis" | \
jq '.[] | .name == "default"' | grep -q true && echo true || echo false)

#Create the search index if it doesn't not exist already
if [ "$is_search_index_exist" = false ]; then
    curl -s -X POST -u "$API_PUBLIC_KEY:$API_PRIVATE_KEY" --digest \
        --header "Accept: application/vnd.atlas.2023-01-01+json" \
        --header "Content-Type: application/json" \
        --data '{
                "collectionName": "apis",
                "database": "APIM_DB",
                "name": "default",
                "type": "search",
                "analyzer": "lucene.standard",
                "mappings": {
                        "dynamic": true
                },
                "searchAnalyzer": "lucene.standard"
        }' \
    "https://cloud.mongodb.com/api/atlas/v2/groups/$PROJECT_ID/clusters/$CLUSTER/fts/indexes"
fi
