FROM openjdk:8-jdk-alpine
LABEL authors="Saisimon"
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/agtms-admin.jar agtms-admin.jar
EXPOSE 7890
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar agtms-admin.jar