package com.cbio.app.service.minio;

import com.cbio.chat.dto.DialogoDTO;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface MinioService {
    void putFile(MultipartFile file, String nome, String repositorio) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    ResponseEntity<InputStreamResource> getResponseEntityFile(String file, String path) throws Exception;

    InputStream getFile(String file, String path) throws Exception;

    void removeDir(String repositorio) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    Map<String, String> getMetadata(String nome, String repositorio) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    ResultGetFileFromMinio getResultGetFileFromMinio(DialogoDTO dialogoDTO) throws Exception;

    String getFileUrl(String fileId, String repositorio) throws ServerException,
            InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
