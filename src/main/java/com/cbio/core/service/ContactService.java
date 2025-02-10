package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.ContactDTO;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface ContactService {

    ContactDTO save(ContactDTO contactDTO) throws CbioException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    ContactDTO update(ContactDTO contactDTO) throws CbioException;
    ContactDTO getContact(String id) throws CbioException;
    List<ContactDTO> getContacts() throws CbioException;
    void delete(String id) throws CbioException;
}
