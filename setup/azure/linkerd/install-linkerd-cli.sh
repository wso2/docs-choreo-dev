#!/usr/bin/env bash

############### Install Linkerd
echo "--- Installing LinkerD CLI..."
linkerd_installed="true"
command -v linkerd >/dev/null 2>&1 || {
    linkerd_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/install | bash
        linkerd_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install linkerd
        linkerd_installed="true"
    else
        echo "Could not install linkerd cli. Unsupported operating system. Please manually install it.."
    fi
}

if [[ "${linkerd_installed}" == "false" ]]; then
    echo "[FAILED] linkerd cli installation. See https://linkerd.io/2.11/getting-started/"
    linkerd_installed=false
fi
