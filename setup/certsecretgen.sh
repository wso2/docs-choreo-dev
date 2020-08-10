#!/usr/bin/env bash
echo "-----------------------------------------"
echo "| Choreo Certificate Secret Generator   |"
echo "-----------------------------------------"

function printusage {
    echo "Usage: $0 [-n=namespace] [-o=outputdir] [--tls-key=key] [--tls-cert=cert] [--secret-name=name]"; \
    echo "   -n=namespace             - namespace for which sealed TLS secret is generated"; \
    echo "   -o=outputdir             - sealed TLS secret output directory";
    echo "   --tls-key=key            - TLS private key";
    echo "   --tls-cert=cert          - TLS certificate";
    echo "   --secret-name=name       - name of the secret";
    echo;
    echo "   e.g. $0 --tls-key=choreo.key --tls-cert=choreo.crt -o=out";
    exit 1;
}

[[ $# -eq 0 ]] &&
{
   printusage
}

while [[ $(kubectl get pods -n kube-system -l name=sealed-secrets-controller -o \
      'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do
 echo "waiting for sealed-secrets-controller to be ready..." && sleep 10;
done

secret_name="ingress-cert"
selfsigned="false"
namespace="default"
outdir="."

for arg in "$@"
do
    case $arg in
        -sn=*|--secret-name=*)
        secret_name="${arg#*=}"
        shift
        ;;
        -n=*|--namespace=*)
        namespace="${arg#*=}"
        shift
        ;;
        -o=*|--output=*)
        outdir="${arg#*=}"
        shift
        ;;
        -s=*|--self-signed=*)
	# shellcheck disable=SC2034
        selfsigned="${arg#*=}"
        shift
        ;;
        -k=*|--tls-key=*)
        tlskey="${arg#*=}"
        shift
        ;;
        -c=*|--tls-cert=*)
        tlscert="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift
        ;;
    esac
done

if [[ (( -z "${tlskey}" ) && ( -n "${tlscert}" )) || (( -n "${tlskey}" ) && ( -z "${tlscert}" )) ]]; then
    printusage
fi

if [[ ( -z "${tlskey}" ) || ( -z "${tlscert}" ) ]]; then # Self signed cert
    mkdir -p "${outdir}"
    ################ Create the ingress certificate (optional)
    echo "--- Generating self signed ingress TLS key & certificate..."
    sudo openssl genrsa -out "${outdir}/tls.key" 2048
    sudo openssl req -new -out "${outdir}/tls.csr" -key "${outdir}/tls.key" -config openssl.cnf
    sudo openssl x509 -req -days 3650 -in "${outdir}/tls.csr" -signkey "${outdir}/tls.key" -out "${outdir}/tls.crt" \
         -extensions v3_req -extfile openssl.cnf

    ############### Import the cert into JRE CA trusted certs to make Java clients work (Optional)
    echo "--- Importing self signed ingress certificate to JRE trust store..."
    echo "Default keystore password = changeit"
    keystore=$JAVA_HOME/jre/lib/security/cacerts
    alias=choreoingress_local${namespace}${secret_name}
    sudo keytool -delete -alias "${alias}" -keystore "${keystore}"
    sudo keytool -import  -alias "${alias}" -keystore "${keystore}" -file "${outdir}/tls.crt" -noprompt

    ############### Create ingress TLS cert sealed secrets for all environments (Optional)
    sudo chown "${USER}" "${outdir}/tls.key"
    sudo chown "${USER}" "${outdir}/tls.crt"

    tlskey=${outdir}/tls.key
    tlscert=${outdir}/tls.crt
fi

echo "--- Creating ingress TLS cert sealed secrets for ${namespace} environment..."
mkdir -p "$outdir"
kubectl create -n "${namespace}" secret tls "${secret_name}" \
       --key "${tlskey}" --cert "${tlscert}" --dry-run=client -o yaml |
       kubeseal --scope strict -o yaml - > "${outdir}/sealed-${secret_name}.yaml"
echo "Sealed secret ingress cert generated and copied to ${outdir}"
