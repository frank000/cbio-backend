package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.ContactDTO;

import java.util.List;

public interface ContactService {

    ContactDTO save(ContactDTO contactDTO) throws CbioException;
    ContactDTO update(ContactDTO contactDTO) throws CbioException;
    ContactDTO getContact(String id) throws CbioException;
    List<ContactDTO> getContacts() throws CbioException;
    void delete(String id) throws CbioException;
}
