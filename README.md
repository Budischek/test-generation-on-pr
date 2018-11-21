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
Visit localhost:8080/uptime to verify functionality

Deploy using Docker
```
//build the jar file
./gradlew build 
//create the docker image
docker build -t testonpr-manual 
// Start up the docker container and connect localhost:8080 to the exposed port
docker run -p 8080:8080 testonpr-manual 
```

### Webhook
While working locally use Serveo to tunnel the webhook requests to your localhost
```
ssh -R cs454:80:localhost:8080 serveo.ne
```

### Development
```
./gradlew bootRun
localhost:8080/{apiEndpoint}
//while developing just use /dev in DevController
localhost:8080/clone
//download test repository
//go into test repository and run build script for .class files
coverageLogicBean.runTests
//this generates jacoco.exec
//get line coverage with:
coverageLogicBean.getProbeActivation(pathToJacoco, classUnderTest)
```