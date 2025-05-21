{{/*
Common labels
*/}}
{{- define "app-service.labels" -}}
choreo.component: app-service
{{- end }}

{{- define "app-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - app-service
        topologyKey: kubernetes.io/hostname
{{- end }}
