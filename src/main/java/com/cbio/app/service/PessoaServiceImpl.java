package com.cbio.app.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cbio.core.service.PessoaService;
import com.cbio.core.v1.dto.PessoaDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PessoaServiceImpl implements PessoaService {
    @Override
    public String buscarPorMatricula(String matricula) throws IOException {

        String matriculaFormatada = formataMatricula(matricula);

        String endpoint = new StringBuilder("https://prd-pessoa.policia.df.gov.br/pessoa/matricula/")
                .append(matriculaFormatada)
                .toString();

        OkHttpClient client = new OkHttpClient();

        String token = "OJbv3V$bj2vW6a";

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("accept", "*/*")
                .addHeader("content-type", "application/json")
                .addHeader("x-pass", token)
                .build();

        ResponseBody response = client.newCall(request).execute().body();

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PessoaDTO pessoa = objectMapper.readValue(response.string(), PessoaDTO.class);

        return new StringBuilder(pessoa.getPostoSigla())
                .append(" ")
                .append(pessoa.getQuadroSigla())
                .append(" ")
                .append(pessoa.getPolicialMatricula())
                .append(" ")
                .append(pessoa.getNome())
                .toString();

    }

    String formataMatricula(String matricula) {

        String temp = matricula.replaceAll("\\.", "");
        temp = temp.replaceAll("\\/", "");

        int cont = temp.length();

        while (cont < 8) {
            temp = "0" + temp;
            cont++;
        }

        return temp;
    }
}
