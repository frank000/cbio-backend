#!/usr/bin/env sh
exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${PROFILE} -jar "app.jar" "$@"
