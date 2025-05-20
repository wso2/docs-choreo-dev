{{/*
Common labels
*/}}
{{- define "api-key-service.labels" -}}
choreo.component: api-key-service
{{- end }}

{{- define "api-key-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - api-key-service
        topologyKey: kubernetes.io/hostname
{{- end }}
