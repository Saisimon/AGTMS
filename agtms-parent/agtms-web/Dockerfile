FROM openjdk:8-jdk-alpine
LABEL authors="Saisimon"
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY waiting.sh waiting.sh
ADD target/agtms-web.jar agtms-web.jar
EXPOSE 7892
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar agtms-web.jar