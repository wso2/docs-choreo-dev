{{/*
Common labels
*/}}
{{- define "spicedb-operator.labels" -}}
choreo.component: spicedb-operator
{{- end }}

{{- define "spicedb-operator.affinity" -}}
affinity:
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
            - key: choreo.component
              operator: In
              values:
                - spicedb-operator
        topologyKey: kubernetes.io/hostname
{{- end }}
