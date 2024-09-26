package com.cbio.app.service;


import com.cbio.app.service.minio.AbstractMinioService;
import com.cbio.app.service.minio.MinioService;
import io.minio.MinioClient;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioServiceImpl extends AbstractMinioService implements MinioService {

    @Value("${minio.api.endpoint}")
    private String endpoint;

    @Value("${minio.api.user}")
    private String user;

    @Value("${minio.api.password}")
    private String password;

    @Value("${minio.bucket}")
    private String bucket;


    @PostConstruct
    private void postConstruct() {
        this.setBucket(bucket);
        this.setMini(MinioClient.builder().endpoint(endpoint).credentials(user, password).build());
    }

    public void putFile(MultipartFile file, String nome, String repositorio) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        super.putFile(file, getChaveDoArquivo(nome, repositorio), repositorio);
    }
    public ResponseEntity<InputStreamResource> getFile(String file, String path) throws Exception {
        return super.getFile(getChaveDoArquivo(file, path));
    }

    @Override
    public void removeDir(String repositorio) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        super.removeDir(repositorio);
    }

    @Override
    public String getChaveDoArquivo (String nome, String repositorio){
        return String.format("%s/%s", repositorio, nome);
    }
}