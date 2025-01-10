#!/bin/bash

# Recebe os parâmetros
BOT_TOKEN=$1
TOKEN=$2
CLIENTE=$3

# Define a URL para a requisição
URL="https://api.telegram.org/bot$BOT_TOKEN/setWebhook?url=https%3A%2F%2Fpleasing-elf-instantly.ngrok-free.app%2Fv1%2Fbot%2Fwebhook%3Ftoken%3D$TOKEN%26cliente%3D$CLIENTE"

# Executa a requisição POST com curl
curl --location --request POST "$URL"