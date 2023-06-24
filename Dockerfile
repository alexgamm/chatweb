FROM openjdk:17-jdk-slim-buster

ENV SERVICE_PATH /opt/chatweb
ENV JAR_NAME chatweb-0.0.1-SNAPSHOT.jar

RUN mkdir ${SERVICE_PATH}

COPY build/libs/${JAR_NAME} ${SERVICE_PATH}

WORKDIR ${SERVICE_PATH}
CMD exec java -jar ${JAR_NAME}
