#!/usr/bin/env bash

############## Install Linkerd
echo "--- Installing Linkerd..."
LINKERD_VERSION=stable-2.9.0
linkerd_installed="true"
command -v linkerd >/dev/null 2>&1 || {
    linkerd_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl -sLO "https://github.com/linkerd/linkerd2/releases/download/$LINKERD_VERSION/linkerd2-cli-$LINKERD_VERSION-linux"
        sudo cp ./linkerd2-cli-$LINKERD_VERSION-linux /usr/local/bin/linkerd
        sudo chmod +x /usr/local/bin/linkerd
        linkerd_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        curl -sLO "https://github.com/linkerd/linkerd2/releases/download/$LINKERD_VERSION/linkerd2-cli-$LINKERD_VERSION-darwin"
        sudo cp ./linkerd2-cli-$LINKERD_VERSION-darwin /usr/local/bin/linkerd
        sudo chmod +x /usr/local/bin/linkerd
        linkerd_installed="true"
    else
        echo "Could not install linkerd. Unsupported operating system. Please manually install it.."
    fi
}

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
if [[ "${kustomize_installed}" == "false" ]]; then
    echo "[FAILED] kustomize installation. See https://github.com/kubernetes-sigs/kustomize/blob/master/docs/INSTALL.md"
    k8s_install_successful=false
fi
if [[ "${k8s_install_successful}" == "true" ]]; then
    echo "Kubernetes cluster initialized successfully"
fi

