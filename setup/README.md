k create -n kube-system secret tls ingress-cert --key tls.key --cert tls.crt --dry-run -o yaml > ingress-cert.yaml
kubeseal --scope strict --cert sealed-secret.crt < ingress-cert.yaml -o yaml  > sealed-ingress-cert.yaml

kubeseal --scope strict < secret.yaml -o yaml  > sealed-secret.yaml