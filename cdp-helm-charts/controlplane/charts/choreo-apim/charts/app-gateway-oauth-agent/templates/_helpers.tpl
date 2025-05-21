{{/*
Common labels
*/}}
{{- define "app-gateway-oauth-agent.labels" -}}
choreo.component: app-gateway-oauth-agent
{{- end }}

{{- define "app-gateway-oauth-agent.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - app-gateway-oauth-agent
        topologyKey: kubernetes.io/hostname
{{- end }}
