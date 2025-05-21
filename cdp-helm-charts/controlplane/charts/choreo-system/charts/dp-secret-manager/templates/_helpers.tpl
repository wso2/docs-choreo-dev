{{/*
Common labels
*/}}
{{- define "dp-secret-manager.labels" -}}
choreo.component: dp-secret-manager
{{- end }}

{{- define "dp-secret-manager.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-secret-manager
        topologyKey: kubernetes.io/hostname
{{- end }}
