package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.google.QueryFilterCalendarDTO;
import com.cbio.core.v1.dto.google.ScheduleDTO;
import com.cbio.core.v1.dto.google.StateDTO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.model.Event;
import io.minio.errors.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface CalendarGoogleService {
    void executa(String id, Credential credentials) throws IOException, GeneralSecurityException;


    String initiateGoogleAuthFlow(StateDTO dto) throws Exception;

    Credential exchangeAndStoreCodeForCredential(String authorizationCode, String companyId) throws IOException, CbioException;

    List<Event> listEvents(String id, String company) throws IOException, CbioException;

    List<EventDTO> listEventsByResource(String id, QueryFilterCalendarDTO dateDTO) throws CbioException, IOException;

    List<EventDTO> listEventsByResourceAndCompanyId(String id, QueryFilterCalendarDTO dateDTO, String companyId) throws CbioException, IOException;

    Map<String, List<ScheduleDTO>> listScheduleByResource(String id, QueryFilterCalendarDTO dateDTO, String companyId) throws CbioException, IOException;

    Map<String, List<ScheduleDTO>> listScheduleByResource(String id, QueryFilterCalendarDTO dateDTO) throws CbioException, IOException;

    void insertEvent(EventDTO dto) throws CbioException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void insertEvent(EventDTO dto, String companyIdUserLogged) throws CbioException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void notifyEvent(EventDTO dto) throws CbioException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void delete(EventDTO dto, String eventId) throws CbioException, IOException;

    void updateEvent(EventDTO dto, String eventId) throws CbioException, IOException;
}