{{/*
Common labels
*/}}
{{- define "apim-devportal.labels" -}}
choreo.component: apim-devportal
{{- end }}

{{- define "apim-devportal.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - apim-devportal
        topologyKey: kubernetes.io/hostname
{{- end }}
