package com.cbio.app.service.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public abstract class AbstractMinioService {


    private String bucket;
    private MinioClient mini = null;

    public abstract String getChaveDoArquivo(String nome, String repositorio);

    public void putFile(MultipartFile file, String chave, String repositorio) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Map<String, String> metadata = new HashMap<>();

        metadata.put("name", file.getOriginalFilename());
        metadata.put("content-type", file.getContentType());

        mini.putObject(PutObjectArgs.builder().bucket(bucket).userMetadata(metadata).object(chave).stream(file.getInputStream(), file.getSize(), -1).build());
    }

    public ResponseEntity<InputStreamResource> getFile(String chave) throws Exception {
        try{
            InputStream file = mini.getObject(GetObjectArgs.builder().bucket(bucket).object(chave).build());
            Map<String, String> metadata = getMetadata(chave);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + metadata.get("name"));
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(file));

        }catch (ErrorResponseException e){
            throw new Exception("Arquivo não encontrado com a chave infomada.");
        }


    }

    public Map<String, String> getMetadata(String chave) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        StatObjectResponse statObjectResponse = mini.statObject(StatObjectArgs.builder().bucket(bucket).object(chave).build());
        return statObjectResponse.userMetadata();
    }

    public List<String> getFiles(String repositorio) {

        Iterable<Result<Item>> results = mini.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(repositorio + "/").recursive(true).includeUserMetadata(true).build());

        List<String> lista = new ArrayList<>();

        results.forEach(e -> {
            try {
                lista.add(download(e.get().objectName()));
            } catch (ErrorResponseException | NoSuchAlgorithmException | ServerException | XmlParserException |
                     InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                     IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return lista;
    }

    private String download(String nomeArquivo) {

        try {

            GetObjectResponse e = mini.getObject(GetObjectArgs.builder().bucket(bucket).object(nomeArquivo).build());


            byte[] sourceBytes = IOUtils.toByteArray(e);

            String type = e.headers().get("X-Amz-Meta-Content-Type");

            return "data:" + type + ";base64," + Base64.getEncoder().encodeToString(sourceBytes);

        } catch (ErrorResponseException | XmlParserException | NoSuchAlgorithmException | IOException |
                 ServerException | InvalidKeyException | InsufficientDataException | InternalException |
                 InvalidResponseException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void removeDir(String dir) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Iterable<Result<Item>> results = mini.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(dir + "/").recursive(true).build());

        List<DeleteObject> objects = new LinkedList<>();

        results.forEach(e -> {
            try {
                objects.add(new DeleteObject(e.get().objectName()));
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException ex) {
                throw new RuntimeException(ex);
            }
        });

        Iterable<Result<DeleteError>> errorResults = mini.removeObjects(
            RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build());

        for (Result<DeleteError> result : errorResults) {
            DeleteError error = result.get();
            System.out.println(
                    "Error in deleting object " + error.objectName() + "; " + error.message());
        }
    }

}
