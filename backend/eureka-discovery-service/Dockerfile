FROM openjdk:16

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/eureka-discovery-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/app.jar

WORKDIR $PROJECT_HOME

CMD ["java" ,"-jar","./app.jar"]