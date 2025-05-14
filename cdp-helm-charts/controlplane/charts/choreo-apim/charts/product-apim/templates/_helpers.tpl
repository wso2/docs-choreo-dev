{{/*
Common labels
*/}}
{{- define "product-apim.labels" -}}
choreo.component: product-apim
{{- end }}

{{- define "product-apim.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - product-apim
        topologyKey: kubernetes.io/hostname
{{- end }}
