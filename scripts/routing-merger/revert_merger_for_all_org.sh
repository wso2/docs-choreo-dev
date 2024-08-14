#!/bin/bash

apis_records_tm=("e1-us-east-azure" "*.e1-us-east-azure" "*.e1-us-east-azure.test")
apis_records_a_records=("customdns.e1-us-east-azure")
apps_records_a_records=("*" "*.e1-us-east-azure" "customdns.e1-us-east-azure")

backup_ip="20.22.170.148"
ttl=60

resource_group="CHOREO-DNS-RG"
subscription="choreo-shared-001"
apis_zone_name="choreoapis.dev"
apps_zone_name="choreoapps.dev"

for record in "${apis_records_tm[@]}"; do
  az network dns record-set cname delete \
    --name "$record" \
    --zone-name "$apis_zone_name" \
    --resource-group "$resource_group" \
    --subscription "$subscription" --yes
  az network dns record-set a add-record \
    --ipv4-address "$backup_ip" \
    --record-set-name "$record" \
    --resource-group "$resource_group" \
    --zone-name "$apis_zone_name" \
    --subscription "$subscription" \
    --ttl "$ttl"
done

for record in "${apis_records_a_records[@]}"; do
  existing_a_record_ip=$(az network dns record-set a show \
                          --name "*" \
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
                          --name "*" \
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
