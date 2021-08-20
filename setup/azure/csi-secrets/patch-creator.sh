#!/usr/bin/env bash

positional=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -i|--input)
    inputFilePath="$2"
    shift
    shift
    ;;
    *)
    positional+=("$1")
    shift
    ;;
esac
done

if [ ! "$inputFilePath" ]
then
    echo "Mandatory arguments are missing"
    exit 1
fi

rm -f secret-provider-class-patch.yaml patch_template.yaml temp_1.txt temp_2.yaml

for filename in choreo-system/secret-provider-class/*.yaml; do
    if [[ $filename != *"kustomization.yaml"* ]]
    then
        printf "\n---\n\n" >> patch_template.yaml
        cat "$filename" >> patch_template.yaml
    fi
done

for filename in choreo-apim/secret-provider-class/*.yaml; do
    if [[ $filename != *"kustomization.yaml"* ]]
    then
        printf "\n---\n\n" >> patch_template.yaml
        cat "$filename" >> patch_template.yaml
    fi   
done

while read -r line || [ -n "$line" ]; do
    if [ -z "$line" ]
    then
      continue
    fi
    temp="${line//-/_}"
    printf "%s\n""$temp" >> temp_1.txt
done <"${inputFilePath}"

# SC1091 check disabled because subjected file is temporary created
# shellcheck disable=SC1091
source temp_1.txt
( echo "cat <<EOF >secret-provider-class-patch.yaml";
  cat patch_template.yaml;
  echo "EOF";
) >temp_2.yaml
# shellcheck disable=SC1091
. temp_2.yaml

rm -f patch_template.yaml temp_1.txt temp_2.yaml
