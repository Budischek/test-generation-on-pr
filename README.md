# Test Generation on Pull Request

Automatically extend the existing test suite to accommodate changes in Pull Requests
## Getting Started

### Prerequisites
Docker version 18

### Workflow
Create a new feature branch for each feature/bugfix, do not merge to master unless code was reviewed by at least one other team member

### Setup
Run on locally
```
./gradlew bootRun
```

Deploy using Docker
```
//build the jar file
./gradlew build 
//create the docker image
docker -t testonpr-manual 
// Start up the docker container and connect localhost:8080 to the exposed port
docker run -p 8080:8080 testonpr-manual 
```
Visit localhost:8080/uptime to verify functionality