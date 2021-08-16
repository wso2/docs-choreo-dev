#!/usr/bin/env bash

positional=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -v|--vault)
    vault="$2"
    shift
    shift
    ;;
    -i|--input)
    inputFilePath="$2"
    shift
    shift
    ;;
    -o|--output)
    outputFilePath="$2"
    shift
    shift
    ;;
    -t|--type)
    type="$2"
    shift
    shift
    ;;
    *)
    positional+=("$1")
    shift
    ;;
esac
done

if [ ! "$vault" ] || [ ! "$inputFilePath" ] || [ ! "$outputFilePath" ] || [ ! "$type" ]
then
    echo "Mandatory arguments are missing"
    exit 1
fi

echo "--- Creating secrets/certificates..."
echo "--- Secrets/certificates will be added to the ${vault} key vault"
echo "" >> "${outputFilePath}"
echo "--- Object versions of the secrets/certificates ---" >> "${outputFilePath}"

counter=0
while read -r line || [ -n "$line" ]; do
    if [ -z "$line" ]
    then
      continue
    fi
    IFS=" " read -r -a inputs <<< "$line"
    secretName=${inputs[0]}
    secretValue=${inputs[1]}
    if [ "$type" = "secret" ]
    then
        output=$(az keyvault secret set --name "${secretName}" --vault-name "${vault}" --value "${secretValue}" --query '["id"][0]')
    elif [ "$type" = "pem" ]
    then
        output=$(az keyvault secret set --name "${secretName}" --vault-name "${vault}" --file "${secretValue}" --query '["id"][0]')
    elif [ "$type" = "cert" ]
    then
        output=$(az keyvault certificate import --name "${secretName}" --vault-name "${vault}" --file "${secretValue}" --query '["id"][0]')
    else
        echo "Unknown type, Please use secret, pem or cert as type"
        break
    fi
    output="${output%\"}"
    output="${output#\"}"
    id=${output##*/}
    echo "${secretName}=${id}" >> "${outputFilePath}"
    echo "--- ${secretName} is created"
    counter=$((counter+1))
done <"${inputFilePath}"

echo "--- Secrets creation/certificates is completed"
echo "--- Total of ${counter} secrets/certificates added"
echo "--- Object versions can be found in ${outputFilePath} file"
echo "-------------------------------------" >> "${outputFilePath}"
