{{/*
Common labels
*/}}
{{- define "resource-authorization-service.labels" -}}
choreo.component: resource-authorization-service
{{- end }}

{{- define "resource-authorization-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - resource-authorization-service
        topologyKey: kubernetes.io/hostname
{{- end }}
