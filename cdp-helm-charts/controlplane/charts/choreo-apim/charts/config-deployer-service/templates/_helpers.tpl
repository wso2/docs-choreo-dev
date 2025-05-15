{{/*
Common labels
*/}}
{{- define "config-deployer-service.labels" -}}
choreo.component: config-deployer-service
{{- end }}

{{- define "config-deployer-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - config-deployer-service
        topologyKey: kubernetes.io/hostname
{{- end }}
