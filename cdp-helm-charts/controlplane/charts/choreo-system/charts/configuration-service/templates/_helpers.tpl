{{/*
Common labels
*/}}
{{- define "configuration-service.labels" -}}
choreo.component: configuration-service
{{- end }}

{{- define "configuration-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - configuration-service
        topologyKey: kubernetes.io/hostname
{{- end }}
