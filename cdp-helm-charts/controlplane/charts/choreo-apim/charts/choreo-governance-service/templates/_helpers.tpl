{{/*
Common labels
*/}}
{{- define "choreo-governance-service.labels" -}}
choreo.component: choreo-governance-service
{{- end }}

{{- define "choreo-governance-service.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - choreo-governance-service
        topologyKey: kubernetes.io/hostname
{{- end }}
