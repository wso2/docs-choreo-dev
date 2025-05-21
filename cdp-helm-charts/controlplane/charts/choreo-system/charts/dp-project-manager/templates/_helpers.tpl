{{/*
Common labels
*/}}
{{- define "dp-project-manager.labels" -}}
choreo.component: dp-project-manager
{{- end }}

{{- define "dp-project-manager.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-project-manager
        topologyKey: kubernetes.io/hostname
{{- end }}
