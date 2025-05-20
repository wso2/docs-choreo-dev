{{/*
Common labels
*/}}
{{- define "subscriptions-service.labels" -}}
choreo.component: subscriptions-service
{{- end }}

{{- define "subscriptions-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - subscriptions-service
        topologyKey: kubernetes.io/hostname
{{- end }}
