{{/*
Common labels
*/}}
{{- define "apim-proxy-deployer.labels" -}}
choreo.component: apim-proxy-deployer
{{- end }}

{{- define "apim-proxy-deployer.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - apim-proxy-deployer
        topologyKey: kubernetes.io/hostname
{{- end }}
