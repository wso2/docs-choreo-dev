#!/usr/bin/env bash

################ Install GH Runners ########
echo "--- Creating namespace actions-runner-system..."
kubectl create namespace "${ENV}-actions-runner-system" --dry-run=client -o yaml | kubectl apply -f -

helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/actions-runner-controller --version 0.20.2

helm upgrade --install actions-runner-controller actions-runner-controller-0.20.2.tgz \
    --version 0.20.2 \
    --namespace "${ENV}-actions-runner-system" \
    --set=authSecret.create=true \
    --set=authSecret.github_token="${GITHUB_TOKEN}" \
    --set=githubWebhookServer.enabled=true \
    --set=githubWebhookServer.ports[0].nodePort=33080 \
    --set=nodeSelector.execution-mode=ghrunner \
    --set=tolerations[0].operator=Exists,tolerations[0].effect=NoSchedule,tolerations[0].key=ghrunner \
    --set=githubWebhookServer.nodeSelector.execution-mode=ghrunner \
    --set=githubWebhookServer.tolerations[0].operator=Exists,githubWebhookServer.tolerations[0].effect=NoSchedule,githubWebhookServer.tolerations[0].key=ghrunner \
    --set=replicaCount=2 \
    --set=githubWebhookServer.replicaCount=2 \
    --set=podDisruptionBudget.enabled=true \
    --set=podDisruptionBudget.minAvailable=50% \
    --set=githubWebhookServer.podDisruptionBudget.enabled=true \
    --set=githubWebhookServer.podDisruptionBudget.minAvailable=50% \
    --set=logLevel="${LOG_LEVEL}" \
    --set=githubWebhookServer.logLevel="${LOG_LEVEL}" \
    --set=image.repository=choreocontrolplane.azurecr.io/summerwind/actions-runner-controller \
    --set=image.actionsRunnerRepositoryAndTag=choreocontrolplane.azurecr.io/summerwind/actions-runner:latest \
    --set=image.dindSidecarRepositoryAndTag=choreocontrolplane.azurecr.io/dind:latest \  ## dind:latest for stg & prod || docker:dind for dev
    --set=metrics.proxy.image.repository=choreocontrolplane.azurecr.io/brancz/kube-rbac-proxy
