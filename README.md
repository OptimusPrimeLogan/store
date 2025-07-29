# The previous README has been moved to [here](README-OLD.md) for reference.
# **Following is the information for the interview panel**.
## Refactoring Summary

The following is a summary of the refactoring work done for the Store application.
- Model changes:
  - The data model has been updated to include a new `Product` entity, which is associated with `Order` entities. 
  - Also `Order` can be searched by ID and `Customer` can be searched by a substring of their name.
  - The `description` field of the `Order` entity has been removed and replaced with `order_number`. which closely resembles a real-world scenario.
- OpenAPI changes:
  - The API has been updated to reflect the new data model, including new endpoints for managing `Product` entities
  - Swagger UI has been integrated to provide a UI for exploring the API.
- Database changes:
  - The faker script and Liquibase changelog has been updated to include sample data for the new `Product` entity.
  - The database schema has been updated using Liquibase to include the new `Product` entity and its relationships with `Order`.
- Caching using L2 cache:
  - Assuming that products are not so frquently changable in this example, the `Product` entity is cached in memory to improve performance.
- CI changes:
  - A CI pipeline (GitHub Actions) has been set up to build the project and deliver it as a Dockerized image, ensuring that the application can be easily deployed and maintained.
  - It has two-step process:
    - Build the application using Gradle and create a Docker image.
    - Push the Docker image to a Docker registry (Docker Hub).
- Testing:
  - The code has been tested with both unit tests and integration tests to ensure that the application behaves as expected.
  - The application has been tested for performance using Gatling, ensuring that it can handle a large number of requests efficiently, even with high latency between the application server and the database.
- Deployment / CD / GitOps:
  - The project has been designed to be easily deployable by using ArgoCD has been used to automate the deployment process.
- Environment setup:
  - The project includes separate application yaml files for different environments (dev, test, prod), allowing for easy configuration and deployment in different environments.

## Instructions for the Interview Panel, post reviewing the refactored code
- The docker run command provided has been moved to the `docker-compose.yml` file, so you can run the application using `docker-compose up` command. 
  - Additionally, the `docker-compose.yml` file includes observability tools like Prometheus and Grafana, which can be used to monitor the application and database, which will be explained later.
  
- Start with deploying the components using the following command:
```shell 
  docker compose up -d
```
- Once done, checkout the code and run the application using the following command:
```shell
  ./gradlew clean bootRun
```
- Once started, you can access the Swagger UI at http://localhost:8080/swagger-ui/index.html to explore the API endpoints.
- The application can be tested for performance using Gatling, which is included in the project under the folder named `gatling` . You can run the performance tests using the following command:
```shell
  ./gradlew gatlingRun --all
```
- The Gatling reports will be generated in the `build/reports/gatling` directory, where you can view the performance results.
- In realtime the folder 'gatling' will be maintained as separate project, so that the performance tests and deployments can be run independently of the application code.
- Metrics are collected using Micrometer `@Timed` annotation, which can be viewed at http://localhost:8080/actuator/prometheus, which must populate the metrics from the test above.
- The metrics are scraped by Prometheus and can be viewed in Grafana, which is also included in the `docker-compose.yml` file.
- You can access Prometheus at http://localhost:9090/targets and Grafana at http://localhost:3000/a/grafana-metricsdrilldown-app/drilldown (default grafana credentials are `admin/admin`).
- There are preconfigured dashboards in Grafana to visualize the metrics that we have generated Eg., [SpringBoot APM Dashboard](https://grafana.com/grafana/dashboards/12900-springboot-apm-dashboard/).

## Continuous Deployment (CD) and GitOps
- The project is designed to be easily deployable in a production environment, in real time the `deploy` folder will be maintained as a separate project per environment (dev, test, prod) 
- In realtime world, the image version can be updated in deployment or values yaml with GitHub Actions, which will trigger a deployment to the respective environment (But not with this example).
- For the interview purposes, I have used microK8s as a local Kubernetes cluster installed in Ubuntu (Can also be done in WSL2 - https://microk8s.io/docs/install-wsl2) . 
- Let us assume you have microK8s installed and running on your local machine. You can deploy the application using the following command:
```shell
    # Enable the required addons in microk8s
    microk8s enable community
    microk8s enable argocd dashboard observability cloudnative-pg
      
    # Apply the Kubernetes manifests for the application, database, observability and ArgoCD
    kubectl apply -f deploy/prod/ --insecure-skip-tls-verify --validate=false
    kubectl apply -f deploy/cnpg/ --insecure-skip-tls-verify --validate=false
    kubectl apply -f deploy/argocd/ --insecure-skip-tls-verify --validate=false
      
    # Either use the service ports or port-forward the services to access them, 
    kubectl port-forward svc/kube-prom-stack-grafana -n observability 3000:80 &
    kubectl port-forward svc/kube-prom-stack-kube-prome-prometheus -n observability 9090:9090 &
    kubectl port-forward -n argocd svc/argo-cd-argocd-server 9000:443 &
    kubectl port-forward svc/store-app-svc -n prod 8080:8080 &    
```
- If you want to see the kubernetes dashboard, run the following command and follow the steps given in the output:
```shell
  microk8s dashboard-proxy
```
- Once completed, the ArgoCD UI can be accessed at http://localhost:9000, default username is admin and password can be retrieved using the following command:
```shell
  kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d
```
- Optional for Ubuntu: Once deployment is completed, Update the Engine.java file with the service URL of the application, so that the Gatling tests can be run against the deployed application.
```shell
  kubectl get service store-app-svc -n prod -o jsonpath='{.spec.clusterIP}'
```
- And then run the Gatling tests using the following command:
```shell
  ./gradlew gatlingRun --all
```
- While the Gatling tests are running, either in ArgoCD or in the Kubernetes dashboard, you can see the application pods are increased to handle the load (min 2 pods, max 4 pods).
- Once the gatlingRun run is complete the metrics and Prometheus can be accessed at http://localhost:9090/targets to see metrics are pushed through [Filtered App Target](http://localhost:9090/targets?search=&scrapePool=serviceMonitor%2Fprod%2Fstore-app-monitor%2F0) .
- Grafana has some preconfigured dashboards to visualize the metrics which can be accessed at http://localhost:3000/dashboards (credentials: admin/prom-operator).
- Swagger is disabled in production, but can be enabled by setting the `springdoc.swagger-ui.enabled` property to `true` in the `application-prod.yaml` file.