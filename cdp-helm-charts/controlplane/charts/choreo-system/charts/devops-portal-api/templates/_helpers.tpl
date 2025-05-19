{{/*
Common labels
*/}}
{{- define "devops-portal-api.labels" -}}
choreo.component: devops-portal-api
{{- end }}

{{- define "devops-portal-api.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - devops-portal-api
        topologyKey: kubernetes.io/hostname
{{- end }}
