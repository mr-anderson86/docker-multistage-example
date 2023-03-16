# First stage: build the application
FROM maven:3.8.3-openjdk-8-slim AS build
USER 1001
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ src/
RUN mvn package
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/host-status.jar"]

# Second stage: create the runtime Docker image
# (Comment out the above "EXPOSE" and "ENTRYPOINT" lines, and uncomment the below lines)

#FROM openjdk:8-alpine3.7
#USER 1001
#WORKDIR /app
#COPY --from=build /app/target/host-status.jar host-status.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app/host-status.jar"]
