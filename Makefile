
prod_java_stop: ## Stop currently running java application
	kill $$(lsof -ti:8080) >/dev/null 2>/dev/null || echo "Not running"

prod_java_run: ## Run java application
	nohup java -jar -Dspring.profiles.active=production ./target/ChessGrinder-*.jar >> java.log &

prod_java_build: ## Build (compile and package java application)
	cd lib && bash install.sh
	mvn clean install -DskipTests

prod_java_redeploy: prod_java_build prod_java_stop prod_java_run ## Kill current java app, build new version and run

prod_js_stop: ## Stop current js (frontend) app
	kill $$(lsof -ti:3000) >/dev/null 2>/dev/null || echo "Not running"

prod_js_build: ## Build frontend application
	cd frontend && npm ci && npm run build

prod_js_run: ## Run frontend application
	cd frontend && nohup serve -s build -p 3000 &

prod_js_redeploy: prod_js_build prod_js_stop prod_js_run ## Kill current frontend app, build new version and run

prod_redeploy: prod_js_redeploy prod_java_redeploy ## Redeploy all

prod_stop: prod_js_stop prod_java_stop ## Stop the whole application

dev_java_stop: ## Stop currently running java application
	kill $$(lsof -ti:8081) >/dev/null 2>/dev/null || echo "Not running"

dev_java_run: ## Run java application
	nohup java -jar -Dspring.profiles.active=development ./target/ChessGrinder-*.jar >> java.log &

dev_java_build: ## Build (compile and package java application)
	cd lib && bash install.sh
	mvn clean install -DskipTests

dev_java_redeploy: dev_java_build dev_java_stop dev_java_run ## Kill current java app, build new version and run

dev_js_stop: ## Stop current js (frontend) app
	kill $$(lsof -ti:3001) >/dev/null 2>/dev/null || echo "Not running"

dev_js_build: ## Build frontend application
	cd frontend && npm ci && npm run build

dev_js_run: ## Run frontend application
	cd frontend && nohup serve -s build -p 3001 &

dev_js_redeploy: dev_js_build dev_js_stop dev_js_run ## Kill current frontend app, build new version and run

dev_redeploy: dev_js_redeploy dev_java_redeploy ## Redeploy all

dev_stop: dev_js_stop dev_java_stop ## Stop the whole application

local_nginx_run: ## Run nginx reverse proxy for local development. Requires docker.
	docker compose -f ./deployment/local/docker-compose.yml up -d nginx

local_nginx_stop: ## Stop local nginx
	docker compose -f ./deployment/local/docker-compose.yml stop nginx

local_postgres_run:
	docker compose -f ./deployment/local/docker-compose.yml up -d postgres

local_postgres_stop:
	docker compose -f ./deployment/local/docker-compose.yml stop postgres

local_stop: local_nginx_stop local_postgres_stop## Stop all services for local development.

help: ## Autogenerated help for Makefile
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

# Allows execute `make` without arguments to get a help
.DEFAULT_GOAL := help

# Forces GNUMake to run task any time it is been called.
.PHONY: *
