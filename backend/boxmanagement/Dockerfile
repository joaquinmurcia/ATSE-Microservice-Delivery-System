FROM openjdk:16

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

# Enable remote debugging
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:9013

COPY target/*.jar $PROJECT_HOME/app.jar
COPY target/classes/public.key $PROJECT_HOME/public.key
# ENV CLASSPATH .;./app.jar


WORKDIR $PROJECT_HOME

CMD ["java" ,"-jar","./app.jar"]