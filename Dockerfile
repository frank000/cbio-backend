# Fase de Build
FROM maven:3-openjdk-17-slim AS build
LABEL maintainer="fraklim.ti@gmail.com"

WORKDIR /home/app


COPY . .

RUN ["mvn", "clean", "package", "-DskipTests=true"]

# Fase Final
FROM openjdk:17-jdk-slim
LABEL maintainer="fraklim.ti@gmail.com"

WORKDIR /app
COPY --from=build /home/app/target/*.jar app.jar

# Configuração do ambiente
ARG PROFILE
ENV PROFILE=${PROFILE}

# Configurar o entrypoint
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

EXPOSE 80
ENTRYPOINT ["./entrypoint.sh"]
