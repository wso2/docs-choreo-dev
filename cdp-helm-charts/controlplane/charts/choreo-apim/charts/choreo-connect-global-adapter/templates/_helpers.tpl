{{/*
Common labels
*/}}
{{- define "choreo-connect-global-adapter.labels" -}}
choreo.component: choreo-connect-global-adapter
{{- end }}

{{- define "choreo-connect-global-adapter.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - choreo-connect-global-adapter
        topologyKey: kubernetes.io/hostname
{{- end }}
