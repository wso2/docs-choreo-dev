{{/*
Common labels
*/}}
{{- define "delete-manager.labels" -}}
choreo.component: delete-manager
{{- end }}

{{- define "delete-manager.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - delete-manager
        topologyKey: kubernetes.io/hostname
{{- end }}
