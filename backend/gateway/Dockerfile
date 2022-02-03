FROM openjdk:16
# TODO: lighten images

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

# Enable remote debugging
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:9014

COPY target/gateway-0.0.1-SNAPSHOT.jar $PROJECT_HOME/app.jar

WORKDIR $PROJECT_HOME

CMD ["java" ,"-jar","./app.jar"]