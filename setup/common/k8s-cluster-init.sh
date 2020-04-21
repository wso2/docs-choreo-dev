#!/usr/bin/env bash

############## Install Reloader
echo "--- Installing Reloader..."
kubectl apply -n kube-system -f reloader.yaml

############## Install Linkerd
echo "--- Installing Linkerd..."
linkerd_installed="true"
command -v linkerd >/dev/null 2>&1 || {
    linkerd_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl -sL https://run.linkerd.io/install | sh
        linkerd_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install linkerd
        linkerd_installed="true"
    else
        echo "Could not install linkerd. Unsupported operating system. Please manually install it.."
    fi
}
linkerd install | kubectl apply -f -

############## Install Sealed secret support
echo "--- Installing kubeseal & Bitnami sealed secrets..."
kubeseal_installed="true"
command -v kubeseal >/dev/null 2>&1 || {
    kubeseal_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        wget https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.12.1/kubeseal-linux-amd64 -O kubeseal
        sudo install -m 755 kubeseal /usr/local/bin/kubeseal
        kubeseal_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install kubeseal
        kubeseal_installed="true"
    else
        echo "Could not install kubeseal. Unsupported operating system. Please manually install it."
    fi
}
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.12.1/controller.yaml
sleep 15


############## Install Kustomize
echo "--- Installing Kustomize..."
kustomize_installed="true"
command -v kustomize >/dev/null 2>&1 || {
    kustomize_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh"  | bash
        kustomize_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install kustomize
        kustomize_installed="true"
    else
        echo "Could not install kustomize. Unsupported operating system. Please manually install it."
    fi
}

k8s_install_successful="true"
if [[ "${linkerd_installed}" == "false" ]]; then
    echo "[FAILED] linkerd installation. See https://linkerd.io/2/getting-started/"
    k8s_install_successful=false
fi
if [[ "${kubeseal_installed}" == "false" ]]; then
    echo "[FAILED] kubeseal installation. See https://github.com/bitnami-labs/sealed-secrets/releases"
    k8s_install_successful=false
fi
if [[ "${kustomize_installed}" == "false" ]]; then
    echo "[FAILED] kustomize installation. See https://github.com/kubernetes-sigs/kustomize/blob/master/docs/INSTALL.md"
    k8s_install_successful=false
fi
if [[ "${k8s_install_successful}" == "true" ]]; then
    echo "Kubernetes cluster initialized successfully"
fi

