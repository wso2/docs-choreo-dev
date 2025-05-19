{{/*
Common labels
*/}}
{{- define "dp-garbage-collector.labels" -}}
choreo.component: dp-garbage-collector
{{- end }}

{{- define "dp-garbage-collector.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-garbage-collector
        topologyKey: kubernetes.io/hostname
{{- end }}
