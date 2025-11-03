{{/*
Expand the name of the chart.
*/}}
{{- define "drinks-app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "drinks-app.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "drinks-app.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "drinks-app.labels" -}}
helm.sh/chart: {{ include "drinks-app.chart" . }}
{{ include "drinks-app.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "drinks-app.selectorLabels" -}}
app.kubernetes.io/name: {{ include "drinks-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "drinks-app.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "drinks-app.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Generate database URL
*/}}
{{- define "drinks-app.databaseUrl" -}}
{{- if .Values.database.external.enabled }}
{{- printf "jdbc:postgresql://%s:%d/%s" .Values.database.external.host .Values.database.external.port .Values.database.external.database }}
{{- else }}
{{- printf "jdbc:postgresql://%s-postgresql:%d/%s" (include "drinks-app.fullname" .) .Values.database.external.port .Values.database.external.database }}
{{- end }}
{{- end }}

{{/*
Generate database username
*/}}
{{- define "drinks-app.databaseUsername" -}}
{{- if .Values.database.external.enabled }}
{{- .Values.database.external.username }}
{{- else }}
{{- .Values.database.external.username | default "postgres" }}
{{- end }}
{{- end }}

