{{/*
Common labels
*/}}
{{- define "dp-mizzen.labels" -}}
choreo.component: dp-mizzen
{{- end }}

{{- define "dp-mizzen.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-mizzen
        topologyKey: kubernetes.io/hostname
{{- end }}
