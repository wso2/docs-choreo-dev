{{/*
Common labels
*/}}
{{- define "connection-service.labels" -}}
choreo.component: connection-service
{{- end }}

{{- define "connection-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - connection-service
        topologyKey: kubernetes.io/hostname
{{- end }}
