#!/bin/bash

# Recebe os parâmetros
IMAGE_NAME=$1
CONTAINER_NAME=$2
DIR_PATH=$3
PORT=$4

FULLCOMMAND="docker stop $CONTAINER_NAME && docker rm $CONTAINER_NAME && docker build -t $IMAGE_NAME $DIR_PATH && docker run -d -p $PORT:5005 --name $CONTAINER_NAME $IMAGE_NAME run"

eval $FULLCOMMAND