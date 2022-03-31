#!/usr/bin/env bash

################ Install GH runner ########
echo "--- Creating namespace actions-runner-system..."
kubectl create namespace actions-runner-system --dry-run=client -o yaml | kubectl apply -f -

helm repo add actions-runner-controller https://actions-runner-controller.github.io/actions-runner-controller

helm install actions-runner-controller actions-runner-controller/actions-runner-controller --namespace actions-runner-system --set=authSecret.create=true \
    --set=authSecret.github_token="${GITHUB_TOKEN}" --set=githubWebhookServer.enabled=true --set=githubWebhookServer.ports[0].nodePort=33080 \
    --set=nodeSelector.execution-mode=ghrunner --set=tolerations[0].operator=Exists,tolerations[0].effect=NoSchedule,tolerations[0].key=ghrunner \
    --set=githubWebhookServer.nodeSelector.execution-mode=ghrunner \
    --set=githubWebhookServer.tolerations[0].operator=Exists,githubWebhookServer.tolerations[0].effect=NoSchedule,githubWebhookServer.tolerations[0].key=ghrunner \
    --set=replicaCount=2 --set=githubWebhookServer.replicaCount=2 \
    --set=podDisruptionBudget.enabled=true --set=podDisruptionBudget.minAvailable=50% --set=githubWebhookServer.podDisruptionBudget.enabled=true --set=githubWebhookServer.podDisruptionBudget.minAvailable=50%

echo "--- Creating runner resources..."
kubectl apply -f runner.yaml
