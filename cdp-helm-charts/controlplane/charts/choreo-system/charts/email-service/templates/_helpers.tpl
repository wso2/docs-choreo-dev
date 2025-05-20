{{/*
Common labels
*/}}
{{- define "email-service.labels" -}}
choreo.component: email-service
{{- end }}

{{- define "email-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - email-service
        topologyKey: kubernetes.io/hostname
{{- end }}
