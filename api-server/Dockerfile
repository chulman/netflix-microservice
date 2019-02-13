FROM java:openjdk-8-jdk

WORKDIR /server/program/bootapp
ADD ./build/libs/api.jar api.jar
EXPOSE 8081

CMD ["/bin/bash", "-c", "exec java -jar api.jar"]
