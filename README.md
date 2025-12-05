# noti-ninja
A playground project the explore and experiment with devops tools and practices.

## Description
A devops-oriented project to show off and explore devops tools and practices. The project focuses on running backend services that perform notification ingestion, analysis and enrichment, and exposing APIs and worker processes for processing and delivery. It is intended as a hands-on lab for CI/CD, infrastructure-as-code, observability, security scanning, and deployment patterns.

## Goals
- Provide a reproducible local development environment (minikube / kind / k3s).
- Demonstrate CI/CD pipelines and automated testing.
- Explore deployment strategies (blue/green, canary).
- Implement observability (metrics, logging, tracing).
- Show common infra tooling (container images, registries, IaC).
- Practice security and policy tooling (image scanners, secrets management, OPA).

## What it will include (common/devops checklist)
- Containerization: Docker images for services
- Local orchestration: Minikube / kind / k3s for local K8s testing
- Kubernetes manifests: Helm charts and/or Kustomize
- CI/CD: GitHub Actions (or Jenkins/Drone) pipelines for build/test/deploy
- Infrastructure as Code: Terraform and/or Pulumi examples
- Artifact registry: Docker registry / GitHub Packages
- Configuration management: Helm values, ConfigMaps, Secrets (sealed / SOPS)
- Secret management: HashiCorp Vault or Kubernetes sealed secrets
- Security scanning: Trivy, Snyk, or similar image/static scanners
- Policy & governance: OPA / Gatekeeper policies
- Service mesh (optional): Linkerd or Istio example
- Observability:
  - Metrics: Prometheus
  - Dashboards: Grafana
  - Logging: EFK (Elasticsearch/Fluentd/Kibana) or Loki/Fluentd/Grafana
  - Tracing: Jaeger / OpenTelemetry
- Testing:
  - Unit tests for services
  - Integration tests (local and CI)
  - End-to-end tests for flows
- Deployment strategies: examples of rolling, blue/green, canary
- Backups & DR: database backup patterns
- RBAC and security hardening examples
- Documentation & examples for everyday devops tasks

## Local development
Start locally using minikube (or kind/k3s). Example:
- Start minikube:
  - minikube start --driver=docker
- Build and load local images:
  - docker build -t noti-ninja-api:local ./api
  - minikube image load noti-ninja-api:local
- Deploy Helm chart or kubectl manifests:
  - helm install noti-ninja ./charts/noti-ninja
- Run tests and view logs via kubectl

Services expected to run locally:
- API service exposing notification analysis endpoints
- Worker/processor service for async processing
- Database (postgres/mysql) and message broker (rabbitmq/kafka) for pipelines
- Observability stack (Prometheus, Grafana, Jaeger)

## Contribution
The project is a playground — contributions, experiments and PRs that add tooling, examples or automation are welcome. Add issues for experiments you want to try and document findings in the repo.

## Monorepo layout

This repository is organized as a monorepo containing the Android app, backend services, DB migrations (Liquibase), Terraform infra, CI/CD templates and other DevOps tooling.

The ASCII tree below is wrapped in a code block so it renders consistently in previews. A short markdown list follows for cleaner rendering in some UI previews.

```text
noti-ninja/
├── apps/
│   ├── android/                  # Android app (Gradle project)
│   │   ├── app/
│   │   └── build.gradle, README.md
│   └── web/                      # optional web/admin UI
├── services/
│   ├── api/                      # REST / GraphQL service
│   │   ├── src/
│   │   ├── Dockerfile
│   │   ├── Makefile
│   │   └── README.md
│   ├── ingestor/                 # ingestion service
│   ├── worker/                   # async processors / consumers
│   └── libs/                     # language-shared libs (auth, telemetry)
├── infra/
│   ├── terraform/                # cloud infra (network, infra resources)
│   ├── k8s/
│   │   ├── charts/               # Helm charts (per-service or umbrella)
│   │   └── manifests/            # raw manifests / kustomize overlays
│   ├── db/
│   │   └── liquibase/            # changelogs + migration tooling
│   └── secrets/                  # SOPS / sealed secrets examples (no secrets checked in)
├── ci/                           # reusable CI scripts and pipeline templates
│   └── pipeline-templates/
├── .github/                      # workflows and GitHub configs
├── scripts/                      # developer helper scripts (local setup)
├── tools/                        # auxiliary tools (local test harnesses, devops helpers)
├── charts/                       # umbrella helm or examples
├── docs/                         # architecture, runbook, onboarding docs
├── tests/                        # e2e / integration test suites
├── build/                        # optional artifact output (ignored in git)
├── LICENSE
└── README.md
```

Top-level summary (markdown-friendly):
- apps/ — mobile and web clients (android, web)
- services/ — backend services (api, ingestor, worker, libs)
- infra/ — terraform, k8s manifests/charts, db migrations (liquibase), secrets examples
- ci/ and .github/ — CI templates and workflows
- scripts/, tools/, charts/, docs/, tests/, build/ — helpers, docs and test harnesses

Add per-package README files and small CI workflow stubs so CI can target only changed areas. Keep secrets out of the repo; use SOPS/sealed secrets for examples and guidance instead.
