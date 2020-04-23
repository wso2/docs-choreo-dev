#!/usr/bin/env bash
echo "-----------------------------------------"
echo "| Choreo Certificate Secret Generator   |"
echo "-----------------------------------------"

function printusage {
    echo "Usage: $0 [-n=namespace] [-o=outputdir] [--self-signed=true/false] [--tls-key=key] [--tls-cert=cert] [--secret-name=name]"; \
    echo "   -n=namespace             - namespace for which sealed TLS secret is generated"; \
    echo "   -o=outputdir             - sealed TLS secret output directory";
    echo "   --tls-key=key            - directory containing secret properties files";
    echo "   --tls-cert=cert          - comma separated environment list";
    echo "   --self-signed=true/false - generate self signed cert";
    echo "   --secret-name=name        - generate self signed cert";
    echo;
    echo "   e.g. $0 --tls-key=choreo.key --tls-cert=choreo.crt -o=out";
    echo "   e.g. $0 --self-signed=true -o=out";
    exit 1;
}

[[ $# -eq 0 ]] &&
{
   printusage
}

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

if [[ ( "${selfsigned}" == "false" ) && (( -z "${tlskey}" ) || ( -z "${tlscert}" )) ]]; then
    printusage
fi

if [[ "${selfsigned}" == "true" ]]; then
    mkdir -p ${outdir}
    ################ Create the ingress certificate (optional)
    echo "--- Generating ingress TLS key & certificate..."
    sudo openssl genrsa -out ${outdir}/tls.key 2048
    sudo openssl req -new -out ${outdir}/tls.csr -key ${outdir}/tls.key -config openssl.cnf
    sudo openssl x509 -req -days 3650 -in ${outdir}/tls.csr -signkey ${outdir}/tls.key -out ${outdir}/tls.crt \
         -extensions v3_req -extfile openssl.cnf

    ############### Import the cert into JRE CA trusted certs to make Java clients work (Optional)
    echo "--- Import ingress certificate to JRE trust store..."
    echo "Default keystore password = changeit"
    keystore=$JAVA_HOME/jre/lib/security/cacerts
    sudo keytool -delete -alias choreoingress_local -keystore ${keystore}
    sudo keytool -import  -alias choreoingress_local -keystore ${keystore} -file ${outdir}/tls.crt -noprompt

    ############### Create ingress TLS cert sealed secrets for all environments (Optional)
    sudo chown ${USER} ${outdir}/tls.key
    sudo chown ${USER} ${outdir}/tls.crt

    tlskey=${outdir}/tls.key
    tlscert=${outdir}/tls.crt
fi

echo "--- Creating ingress TLS cert sealed secrets for ${namespace} environment..."
mkdir -p $outdir
kubectl create -n ${namespace} secret tls ${secret_name} \
       --key ${tlskey} --cert ${tlscert} --dry-run=client -o yaml |
       kubeseal --scope strict -o yaml - > ${outdir}/sealed-${secret_name}.yaml
echo "Sealed secret ingress cert generated and copied to "${outdir}