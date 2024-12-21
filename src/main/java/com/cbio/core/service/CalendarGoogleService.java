package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.google.QueryFilterCalendarDTO;
import com.cbio.core.v1.dto.google.ScheduleDTO;
import com.cbio.core.v1.dto.google.StateDTO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface CalendarGoogleService {
    void executa(String id, Credential credentials) throws IOException, GeneralSecurityException;


    String initiateGoogleAuthFlow(StateDTO dto) throws Exception;

    Credential exchangeAndStoreCodeForCredential(String authorizationCode, String companyId) throws IOException, CbioException;

    List<Event> listEvents(String id, String company) throws IOException, CbioException;

    List<EventDTO> listEventsByResource(String id, QueryFilterCalendarDTO dateDTO) throws CbioException, IOException;

    Map<String, List<ScheduleDTO>> listScheduleByResource(String id, QueryFilterCalendarDTO dateDTO) throws CbioException, IOException;

    void insertEvent(EventDTO dto) throws CbioException, IOException;

    void notifyEvent(EventDTO dto) throws CbioException, IOException;

    void delete(EventDTO dto, String eventId) throws CbioException, IOException;

    void updateEvent(EventDTO dto, String eventId) throws CbioException, IOException;
}