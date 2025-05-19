{{/*
Common labels
*/}}
{{- define "cp-graphql.labels" -}}
choreo.component: cp-graphql
{{- end }}

{{- define "cp-graphql.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - cp-graphql
        topologyKey: kubernetes.io/hostname
{{- end }}
