{{/*
Common labels
*/}}
{{- define "choreo-linter-service.labels" -}}
choreo.component: choreo-linter-service
{{- end }}

{{- define "choreo-linter-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - choreo-linter-service
        topologyKey: kubernetes.io/hostname
{{- end }}
