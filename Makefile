CLUSTER_NAME=noti-ninja-dev
KIND_CONFIG=infra/kind/cluster.yaml

.PHONY: cluster delete-cluster install-ingress cluster-info

cluster:
	@if kind get clusters | grep -q $(CLUSTER_NAME); then \
		echo "Cluster already exists."; \
	else \
		echo ">>> Creating kind cluster..."; \
		kind create cluster --config $(KIND_CONFIG); \
	fi

delete-cluster:
	@echo ">>> Deleting kind cluster..."
	kind delete cluster --name $(CLUSTER_NAME)

cluster-info:
	@echo ">>> Cluster info:"
	kubectl cluster-info
	kubectl get nodes -o wide

install-ingress:
	@echo ">>> Installing NGINX ingress controller..."
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
	@echo ">>> Waiting for ingress controller to be ready..."
	kubectl wait --namespace ingress-nginx \
		--for=condition=ready pod \
		--selector=app.kubernetes.io/component=controller \
		--timeout=120s
