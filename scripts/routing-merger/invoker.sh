#!/bin/bash

keda_no_custom_domain_greeting_service=""
no_keda_custom_domain_greeting_service=""

keda_no_custom_domain_webapp=""
no_keda_custom_domain_webapp=""

internal_api_proxy=""

namespace="prod-choreo-apim"
nginx_port=63000
internal_nginx_port=63001

kubectl port-forward -n "$namespace" svc/choreo-nginx-service "$nginx_port":9443 >/dev/null 2>&1 &
sleep 5
kubectl port-forward -n "$namespace" svc/choreo-internal-nginx-service "$internal_nginx_port":443 >/dev/null 2>&1 &
sleep 5

invoke_externally_apps() {
  local url

  url="$1"
  curl -s -o /dev/null -I -w "%{http_code}" "$url"
}

invoke_internally_apps() {
  local url no_protocol_url

  url="$1"
  no_protocol_url="${url#*://}"

  curl -s --http1.1 -o /dev/null -I -w "%{http_code}" "https://localhost:$nginx_port" -H 'accept: application/json' -H "Host: $no_protocol_url" -k
}

invoke_externally_api() {
  local greeter_url

  greeter_url="$1"

  curl -sX 'GET' \
      "$greeter_url/greeter/greet?name=hello" \
      -H 'accept: text/plain'
}

invoke_internally_api() {
  local greeter_url no_protocol_url api_resource_path base_url

  greeter_url="$1"
  no_protocol_url="${greeter_url#*://}"
  api_resource_path="$(echo "$no_protocol_url" | cut -d'/' -f2-)"
  base_url="$(echo "$no_protocol_url" | cut -d'/' -f1)"

  curl -skX 'GET' --http1.1 \
       "https://localhost:$nginx_port/$api_resource_path/greeter/greet?name=hello" \
       -H 'accept: text/plain' \
       -H "Host: $base_url"
}

invoke_internal_api_proxy() {
  local proxy_url no_protocol_url api_resource_path base_url

  proxy_url="$1"
  no_protocol_url="${proxy_url#*://}"
  api_resource_path="$(echo "$no_protocol_url" | cut -d'/' -f2-)"
  base_url="$(echo "$no_protocol_url" | cut -d'/' -f1)"

  curl -sk \
         "https://localhost:$internal_nginx_port/$api_resource_path" \
         -H 'accept: text/plain' \
         -H "Host: $base_url"
}

while true; do
  keda_no_custom_domain_greeting_service_response_external=$(invoke_externally_api "$keda_no_custom_domain_greeting_service")
  echo "No Custom Domain(keda) External Response: $keda_no_custom_domain_greeting_service_response_external"
  sleep 1
  keda_no_custom_domain_greeting_service_response_internal=$(invoke_internally_api "$keda_no_custom_domain_greeting_service")
  echo "No Custom Domain(keda) Internal Response: $keda_no_custom_domain_greeting_service_response_internal"
  sleep 1
  no_keda_custom_domain_greeting_service_response=$(invoke_externally_api "$no_keda_custom_domain_greeting_service")
  echo "Custom Domain(no keda) Response: $no_keda_custom_domain_greeting_service_response"
  sleep 1

  keda_no_custom_domain_webapp_external_response=$(invoke_externally_apps "$keda_no_custom_domain_webapp")
  echo "Webapp No Custom Domain(keda) External Response: $keda_no_custom_domain_webapp_external_response"
  sleep 1
  keda_no_custom_domain_webapp_internal_response=$(invoke_internally_apps "$keda_no_custom_domain_webapp")
  echo "Webapp No Custom Domain(keda) Internal Response: $keda_no_custom_domain_webapp_internal_response"
  sleep 1
  no_keda_custom_domain_webapp_response=$(invoke_externally_apps "$no_keda_custom_domain_webapp")
  echo "Webapp Custom Domain(no keda) Response: $no_keda_custom_domain_webapp_response"
  sleep 1

  internal_api_proxy_response=$(invoke_internal_api_proxy "$internal_api_proxy")
  echo "Internal API Proxy Response: $internal_api_proxy_response"
  sleep 1
done
