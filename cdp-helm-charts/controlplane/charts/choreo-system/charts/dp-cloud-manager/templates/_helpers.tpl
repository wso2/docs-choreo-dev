{{/*
Common labels
*/}}
{{- define "dp-cloud-manager.labels" -}}
choreo.component: dp-cloud-manager
{{- end }}

{{- define "dp-cloud-manager.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-cloud-manager
        topologyKey: kubernetes.io/hostname
{{- end }}
