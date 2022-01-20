FROM openjdk:16

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/*.jar $PROJECT_HOME/app.jar
COPY target/classes/public.key $PROJECT_HOME/public.key
# ENV CLASSPATH .;./app.jar


WORKDIR $PROJECT_HOME

CMD ["java" ,"-jar","./app.jar"]