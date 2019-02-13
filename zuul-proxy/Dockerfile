FROM java:openjdk-8-jdk

WORKDIR /ws-server/program/bootapp
ADD ./build/libs/gateway.jar gateway.jar
EXPOSE 8080

CMD ["/bin/bash", "-c", "exec java $JAVA_OPTS -jar gateway.jar"]
