{{/*
Common labels
*/}}
{{- define "audit-logging.labels" -}}
choreo.component: audit-logging
{{- end }}

{{- define "audit-logging.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - audit-logging
        topologyKey: kubernetes.io/hostname
{{- end }}
