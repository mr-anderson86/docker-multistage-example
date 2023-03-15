# docker-multistage-example

## Description:

This repository represents a simple Java spring web application.  
Originally, initiated using this website: https://start.spring.io/ it's a very good site, highly recommended.  
  
The purpose of this project is to show the Docker Multi Stage build concept and use case.  
The way to do it is to build the Docker image twice: once without multi-stage build, and the other with it, and then compare between the images.  

### Prerequisites
* Docker (preferably on Linux/Mac, but also could work for Windows)  

### Usage
#### First step: without multi-stage build
```bash
git clone https://github.com/mr-anderson86/docker-multistage-example.git
cd docker-multistage-example
docker build -t spring-host-status:v1.0.0 .
docker images
# You'll see that the image is ~400 MB
docker run -d --name <container-name> -p 8080:8080 spring-host-status:1.0.0
```
Access the web page at http://localhost:8080

#### Second step: with multi-stage build
```bash
docker rm -f <container-name>
vi Dockerfile
# Comment the "EXPOSE" and "ENTRYPOINT" lines from the first stage
# Uncomment the lines of the second stage, save and quit
docker build -t spring-host-status:v1.0.1 .
docker images
# You'll see that the new image is only ~110 MB
# The runtime doesn't need the packages/libraries it used only for the compilation stage
# It needs only the host-status.jar file - so it saves a lot of space.
docker run -d --name <container-name> -p 8080:8080 spring-host-status:1.0.1
```
Access the web page at http://localhost:8080  
  
### Cleanup
```bash
docker rm -f <container-name>
docker rmi spring-host-status:1.0.0 spring-host-status:1.0.1
```

### The end, enjoy :-)