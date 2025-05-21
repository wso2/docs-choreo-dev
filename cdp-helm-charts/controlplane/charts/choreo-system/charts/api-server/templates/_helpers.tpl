{{/*
Common labels
*/}}
{{- define "api-server.labels" -}}
choreo.component: api-server
{{- end }}

{{- define "api-server.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - api-server
        topologyKey: kubernetes.io/hostname
{{- end }}
