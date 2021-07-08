#!/usr/bin/env bash

positional=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
     -n|--name)
    group_name="$2"
    shift
    shift
    ;;
    --org)
    org="$2"
    shift
    shift
    ;;
    -p|--project)
    project="$2"
    shift
    shift
    ;;
    -i|--input)
    inputFilePath="$2"
    shift
    shift
    ;;
    -s|--secret)
    secret="$2"
    shift
    shift
    ;;
    *)
    positional+=("$1")
    shift
    ;;
esac
done

if [ ! "$group_name" ] || [ ! "$org" ] || [ ! "$project" ] || [ ! "$inputFilePath" ]
then
    echo "Mandatory arguments are missing"
    exit 1
fi

if [ ! "$secret" = false ] && [ ! "$secret" = true ]
then
    echo "Secret argument requires a boolean value"
    exit 1
fi

type="Secret variable/s"
if [ "$secret" = false ]
then
  type="Non-Secret variable/s"
fi 

az extension add --name azure-devops

id=$(az pipelines variable-group list --org "${org}" --project "${project}" --group-name "${group_name}" --query '[0]."id"')
if [ -n "$id" ]
then
   echo "Variable group ${group_name} already exists..." 
   echo "New ${type} will be added to the existing group..."
else
   echo "Variable group ${group_name} does not exist..."
   id=$(az pipelines variable-group create --name "${group_name}" --org "${org}" --project "${project}" --variables initialVar=initialVal --query '["id"][0]')
   echo "Variable group ${group_name} is created..."
   echo "New ${type} will be added to the created group..."
fi

counter=0
while read -r line || [ -n "$line" ]; do
    if [ -z "$line" ]
    then
      continue
    fi
    IFS=" " read -r -a inputs <<< "$line"
    name=${inputs[0]}
    value=${inputs[1]}
    az pipelines variable-group variable create --group-id "${id}" --name "${name}" --org "${org}" --project "${project}" --secret "${secret}" --value "${value}"
    echo "${type} ${name} is created"
    counter=$((counter+1))
done <"${inputFilePath}"

echo "--- Variable adding is completed"
echo "--- Total of ${counter} variables added"
