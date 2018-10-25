# Test Generation on Pull Request

Automatically extend the existing test suite to accommodate changes in Pull Requests
## Getting Started

### Prerequisites
Docker version 18

### Setup

```
./gradlew build //build the jar file
docker -t testonpr-manual //create the docker image
docker run -p 8080:8080 testonpr-manual // Start up the docker container and connect localhost:8080 to the exposed port
```
Visit localhost:8080/uptime to verify functionality