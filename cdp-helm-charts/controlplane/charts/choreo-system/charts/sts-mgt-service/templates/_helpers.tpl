{{/*
Common labels
*/}}
{{- define "sts-mgt-service.labels" -}}
choreo.component: sts-mgt-service
{{- end }}

{{- define "sts-mgt-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - sts-mgt-service
        topologyKey: kubernetes.io/hostname
{{- end }}
