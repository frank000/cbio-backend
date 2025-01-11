#!/bin/bash

# Recebe os parâmetros
BOT_TOKEN=$1
TOKEN=$2
CLIENTE=$3

# Define a URL para a requisição
URL="https%3A%2F%2Fapi.telegram.org%2Fbot%24BOT_TOKEN%2FsetWebhook%3Furl%3Dhttps%3A%2F%2Fbot.rayzatec.com.br%2Fapi%2Fv1%2Fbot%2Fwebhook%3Ftoken%3D%24TOKEN%26cliente%3D%24CLIENTE"

# Executa a requisição POST com curl
curl --location --request POST "$URL"