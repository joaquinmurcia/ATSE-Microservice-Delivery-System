FROM openjdk:16

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/usermngmt-0.0.1-SNAPSHOT.jar $PROJECT_HOME/app.jar
COPY target/classes/ase_project.keystore $PROJECT_HOME/ase_project.keystore

WORKDIR $PROJECT_HOME

CMD ["java" ,"-jar","./app.jar"]