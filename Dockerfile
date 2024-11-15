FROM maven:3-openjdk-17-slim AS build
LABEL maintainer="your.email@example.com"
VOLUME /tmp

WORKDIR /home/app


COPY . .

RUN mvn clean package  -X --settings ./m2/settings.xml -D skipTests=true

# Fase final
FROM openjdk:17-jdk-slim
VOLUME /tmp

EXPOSE 8082

RUN mkdir /app
WORKDIR /app

ARG JAR_FILE=app.jar
COPY --from=build /home/app /app
COPY --from=build /home/app/target/*.jar /app/app.jar

ARG PROFILE
ENV PROFILE=$PROFILE

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh

EXPOSE 8082

ENTRYPOINT ["./entrypoint.sh"]