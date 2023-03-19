# Docker Multi-Stage Build Example

## Description

This repository represents a simple Java spring web application.  
Originally, initiated using this website: https://start.spring.io/ it's a very good site, highly recommended.  
This app simulates a host activity monitor (such as Windows "Task Manager", Mac "Activity Monitor", or Prometheus + Grafana).  
  
The purpose of this project is to show the `Docker Multi Stage Build` concept and use case.  
The way to show this is to build the Docker image twice: once [without multi-stage build](#first-step-without-multi-stage-build), and the other [with it](#second-step-with-multi-stage-build), and then compare between the images.  
  
In addition, added a little [bonus](#bonus): modifying the page refresh interval via altering the container's environment variable. :-)

### Screenshot
<img src="screenshot.png" alt="screenshot" width="375" height="375">

### Prerequisites
* Docker  
(preferably on Linux/Mac, since the app uses Linux commands like `free`, `uptime`, `df`, etc...)  

### Usage
#### First step: without multi-stage build
```bash
git clone https://github.com/mr-anderson86/docker-multistage-example.git
cd docker-multistage-example
docker build -t spring-host-status:v1.0.0 .
docker images
# You'll see that the image is ~400 MB

docker run -d --name my-app -p 8080:8080 spring-host-status:v1.0.0
```
Access the web page at http://localhost:8080

#### Second step: with multi-stage build
```bash
# Delete the previous container
docker rm -f my-app

vi Dockerfile
# Comment the "EXPOSE" and "ENTRYPOINT" lines from the first stage
# Uncomment the lines of the second stage, save and quit

docker build -t spring-host-status:v1.0.1 .
docker images
# You'll see that the new image is only ~110 MB
# The runtime doesn't need the packages/libraries used for the compilation stage
# It needs only the host-status.jar file - so it saves a lot of space.

docker run -d --name my-app -p 8080:8080 spring-host-status:v1.0.1
```
Access the web page at http://localhost:8080  

### Bonus
As you probably have seen, the page is refreshing every 2.5 seconds by default.  
But you can override it by altering an envirinment variable in the container:
```bash
# Delete the previous container
docker rm -f my-app

# Let's change the refresh interval to every 5 seconds (you can choose your own nuber if you wish)
docker run -d --name my-app -p 8080:8080 -e REFRESH_INTERVAL=5 spring-host-status:v1.0.1
```
Access the web page at http://localhost:8080  
See that the page is now refreshing at a different interval than 2.5 seconds? :-)  
In addition, you can see the value of the envirinment variable inside the container:
```bash
docker exec -it my-app sh
echo $REFRESH_INTERVAL
# 5, or whichever value you provided above.

exit
# Will exit the container back into your host/computer
```

### Cleanup
```bash
docker rm -f my-app
docker rmi spring-host-status:v1.0.0 spring-host-status:v1.0.1
```

### The end, enjoy :-)