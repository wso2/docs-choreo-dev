{{/*
Common labels
*/}}
{{- define "dp-cicd.labels" -}}
choreo.component: dp-cicd
{{- end }}

{{- define "dp-cicd.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - dp-cicd
        topologyKey: kubernetes.io/hostname
{{- end }}
