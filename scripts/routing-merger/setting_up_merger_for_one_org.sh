#!/bin/bash

apis_record_set_name="260011ca-f51c-4e90-a64c-170ff85d354d-dev.e1-eu-north-azure"
apps_record_set_name1="d23fa96f-0ca0-456f-bca2-fa5f8d8d2539.e1-eu-north-azure"
apps_record_set_name2="ceb2e58d-b6bf-4eab-8b73-e4386877c979.e1-us-east-azure"
backup_ip="20.166.183.117"

resource_group="CHOREO-DNS-RG"
subscription="choreo-shared-001"
ttl=60
apis_zone_name="choreoapis.dev"
apps_zone_name="choreoapps.dev"

az network dns record-set a add-record \
  --ipv4-address "$backup_ip" \
  --record-set-name "$apis_record_set_name" \
  --resource-group "$resource_group" \
  --zone-name "$apis_zone_name" \
  --subscription "$subscription" \
  --ttl "$ttl"

az network dns record-set a add-record \
  --ipv4-address "$backup_ip" \
  --record-set-name "$apps_record_set_name1" \
  --resource-group "$resource_group" \
  --zone-name "$apps_zone_name" \
  --subscription "$subscription" \
  --ttl "$ttl"

az network dns record-set a add-record \
  --ipv4-address "$backup_ip" \
  --record-set-name "$apps_record_set_name2" \
  --resource-group "$resource_group" \
  --zone-name "$apps_zone_name" \
  --subscription "$subscription" \
  --ttl "$ttl"


while true; do
    resolved_ip_apis=$(nslookup "$apis_record_set_name.$apis_zone_name" | grep 'Address' | tail -n 1 | awk '{print $2}')
    resolved_ip_apps1=$(nslookup "$apps_record_set_name1.$apps_zone_name" | grep 'Address' | tail -n 1 | awk '{print $2}')
    resolved_ip_apps2=$(nslookup "$apps_record_set_name2.$apps_zone_name" | grep 'Address' | tail -n 1 | awk '{print $2}')
    if [ "$resolved_ip_apis" == "$backup_ip" ] && [ "$resolved_ip_apps1" == "$backup_ip" ] && [ "$resolved_ip_apps2" == "$backup_ip" ]; then
        echo "The hostnames has resolved to the target IP $backup_ip."
        break
    else
        echo "The hostnames has not yet resolved to the target IP $backup_ip. Current IP: $resolved_ip_apis, $resolved_ip_apps1, $resolved_ip_apps2"
    fi
    sleep 60
done
