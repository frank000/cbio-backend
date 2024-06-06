{{- define "toYaml" -}}
  {{- range $key, $value := . -}}
    {{ if not $value }}
{{ $key }}: ""
    {{- else }}
      {{- $map := kindIs "map" $value -}}
      {{- if $map }}
{{ $key }}:
  {{- include "toYaml" $map | indent 2 }}
      {{- else }}
{{ $key }}: {{ $value }}
      {{- end }}
	{{- end }}
  {{- end -}}
{{- end -}}