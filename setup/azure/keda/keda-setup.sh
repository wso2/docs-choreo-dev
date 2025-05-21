#!/bin/bash

# Set the namespace, configmap name, and configmap yaml file
namespace_name="keda"
configmap_name="keda-http-add-on-routing-table"
configmap_yaml="keda-http-cm.yaml"

# Check if the namespace already exists
if kubectl get namespace "$namespace_name" &> /dev/null; then
    echo "namespace $namespace_name already exists."
else
    # Create the namespace
    kubectl create namespace "$namespace_name"
fi

# Check if the configmap already exists
if kubectl get cm "$configmap_name" -n "$namespace_name" &> /dev/null; then
    echo "configmap $configmap_name already exists."
else
    # Create the configmap
    kubectl apply -f "$configmap_yaml" -n keda
fi
