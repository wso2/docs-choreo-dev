{{/*
Common labels
*/}}
{{- define "dp-rudder.labels" -}}
choreo.component: dp-rudder
{{- end }}

{{- define "dp-rudder.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-rudder
        topologyKey: kubernetes.io/hostname
{{- end }}
