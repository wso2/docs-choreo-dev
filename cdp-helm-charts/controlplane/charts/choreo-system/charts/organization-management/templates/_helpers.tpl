{{/*
Common labels
*/}}
{{- define "organization-management.labels" -}}
choreo.component: organization-management
{{- end }}

{{- define "organization-management.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - organization-management
        topologyKey: kubernetes.io/hostname
{{- end }}
