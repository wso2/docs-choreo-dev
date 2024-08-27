echo "Downloading the db-names.txt file to get the availale db names for the validation process"

source credentials.sh

key=$(az storage account keys list --account-name "$storage" --resource-group "$resourceGroup" --subscription "$subscriptionId" -o json --query [0].value | tr -d '"')

az storage blob download -c dbnames -n db-names.txt --account-name "$storage" --account-key "$key" > db-names-check.txt

databases=()
failed_exports=()

# Read the file line by line and add each line to the array
echo "Reading the file data and adding the database names to an array"
while IFS= read -r line; do
  databases+=("$line")
done < db-names-check.txt

for element in "${databases[@]}"
do
    json_content=$(jq . < "${element}.json")
    parameter_value=$(jq -r '.status' <<< "$json_content")
    if [[ "$parameter_value" == "Completed" ]]; then
        echo "DB ${element} exported correctly"
    else
        failed_exports+=("$element")
        echo "DB ${element} does not exported correctly"
    fi

done


echo "Databases failed to export......"
echo "................................."
echo "................................."

for element in "${failed_exports[@]}"
do
    echo "${element} failed to export"
done