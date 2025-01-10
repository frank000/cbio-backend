FROM maven:3-openjdk-17-slim AS build
LABEL maintainer="your.email@example.com"
VOLUME /tmp
ARG GH_KEY
ARG GH_KEY_AUTH
WORKDIR /home/app

# Copiar apenas o pom.xml primeiro
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Baixar as dependências
RUN mvn dependency:go-offline

# Agora copiar o código fonte
COPY src src

# Fazer o build
RUN ["mvn", "clean", "package", "-DskipTests=true"]

# Fase final
FROM openjdk:17-jdk-slim
VOLUME /tmp

RUN mkdir /app
WORKDIR /app

ARG JAR_FILE=app.jar
COPY --from=build /home/app/target/*.jar /app/app.jar

ARG PROFILE
ENV PROFILE=$PROFILE

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh

EXPOSE 8082

ENTRYPOINT ["./entrypoint.sh"]