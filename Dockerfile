FROM openjdk:8-jdk-alpine
COPY ./build/libs/test-on-pr-0.0.1-SNAPSHOT.jar /usr/src/test-on-pr
WORKDIR /usr/src/test-on-pr
EXPOSE 8080
CMD ["java", "-jar", "test-on-pr-0.0.1-SNAPSHOT.jar"]
