{{/*
Common labels
*/}}
{{- define "choreo-console.labels" -}}
choreo.component: choreo-console
{{- end }}

{{- define "choreo-console.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - choreo-console
        topologyKey: kubernetes.io/hostname
{{- end }}
