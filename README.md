# Test Generation on Pull Request

Automatically extend the existing test suite to accommodate changes in Pull Requests
## Getting Started

### Prerequisites
Docker version 18

### Setup

```
//build the jar file
./gradlew build 
//create the docker image
docker -t testonpr-manual 
// Start up the docker container and connect localhost:8080 to the exposed port
docker run -p 8080:8080 testonpr-manual 
```
Visit localhost:8080/uptime to verify functionality