## Creating Azure DNS Secret for DNS01 CHALLENGE

```bash
kubectl create secret generic "choreo-secret-azuredns-config" --from-literal=client-secret="xxxxxxxxxxxxxxxxxxxxx" -n cert-manager --dry-run=client -oyaml | kubectl apply -f -
```

Install the Cluster issuer. Follow [the sample cluster issuer](./sample-cluster-issuer.yaml)

```bash
kubectl apply -f cluster-issuer.yaml
```

Install Certificate. Follow [the sample certificate](./sample-certificate.yaml)
Add the reflector annotations to state the namespaces the tls secret should be
copied. [Ref](https://github.com/emberstack/kubernetes-reflector/tree/v5.0.10)

```bash
kubectl apply -f certificate.yaml
```