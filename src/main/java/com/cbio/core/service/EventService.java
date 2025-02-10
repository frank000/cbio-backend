package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface EventService {

    void alterNotify(String id);

    void notify(String id) throws CbioException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

}
