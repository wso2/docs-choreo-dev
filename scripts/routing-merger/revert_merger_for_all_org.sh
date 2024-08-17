#!/bin/bash

apis_records_a_records=("*.e1-eu-north-azure.test" "*.e1-eu-north-azure" "customdns.e1-eu-north-azure")
apps_records_a_records=("*.e1-eu-north-azure" "*.ne" "customdns.e1-eu-north-azure")

backup_ip="20.166.183.117"
ttl=60

resource_group="CHOREO-DNS-RG"
subscription="choreo-shared-001"
apis_zone_name="choreoapis.dev"
apps_zone_name="choreoapps.dev"


for record in "${apis_records_a_records[@]}"; do
  existing_a_record_ip=$(az network dns record-set a show \
                          --name "$record" \
                          --zone-name "$apis_zone_name" \
                          --resource-group "$resource_group" \
                          --subscription "$subscription" \
                          --query "ARecords[0].ipv4Address" -o tsv)
  az network dns record-set a add-record \
    --ipv4-address "$backup_ip" \
    --record-set-name "$record" \
    --zone-name "$apis_zone_name" \
    --resource-group "$resource_group" \
    --subscription "$subscription" \
    --ttl "$ttl"
  az network dns record-set a remove-record \
    --ipv4-address "$existing_a_record_ip" \
    --record-set-name "$record" \
    --zone-name "$apis_zone_name" \
    --resource-group "$resource_group" \
    --subscription "$subscription"
done

for record in "${apps_records_a_records[@]}"; do
  existing_a_record_ip=$(az network dns record-set a show \
                          --name "$record" \
                          --zone-name "$apps_zone_name" \
                          --resource-group "$resource_group" \
                          --subscription "$subscription" \
                          --query "ARecords[0].ipv4Address" -o tsv)
  az network dns record-set a add-record \
    --ipv4-address "$backup_ip" \
    --record-set-name "$record" \
    --zone-name "$apps_zone_name" \
    --resource-group "$resource_group" \
    --subscription "$subscription" \
    --ttl "$ttl"
  az network dns record-set a remove-record \
    --ipv4-address "$existing_a_record_ip" \
    --record-set-name "$record" \
    --zone-name "$apps_zone_name" \
    --resource-group "$resource_group" \
    --subscription "$subscription"
done
