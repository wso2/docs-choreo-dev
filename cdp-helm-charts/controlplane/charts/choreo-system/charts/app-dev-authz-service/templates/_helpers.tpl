{{/*
Common labels
*/}}
{{- define "app-dev-authz-service.labels" -}}
choreo.component: app-dev-authz-service
{{- end }}

{{- define "app-dev-authz-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - app-dev-authz-service
        topologyKey: kubernetes.io/hostname
{{- end }}
