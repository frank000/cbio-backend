package com.cbio.app.service;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.app.entities.EventEntity;
import com.cbio.app.entities.GoogleCredentialEntity;
import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.CompanyConfigRepository;
import com.cbio.app.repository.EventRepository;
import com.cbio.app.repository.GoogleCredentialRepository;
import com.cbio.app.service.mapper.EventMapper;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ResourceDTO;
import com.cbio.core.v1.dto.google.CredentialData;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.google.QueryFilterCalendarDTO;
import com.cbio.core.v1.dto.google.StateDTO;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarGoogleServiceImpl implements CalendarGoogleService {

    private final GoogleCredentialRepository googleCredentialRepository;
    private final AuthService authService;
    private final CompanyService companyService;
    private final ResourceService resourceService;
    private final EventRepository eventRepository;
    private final EventService eventService;

    private final EventMapper eventMapper;
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);

    private final CompanyConfigRepository companyConfigRepository;


    public void executa(String id, Credential credentials) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        // List the next 10 events from the primary calendar.
//        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime now = new DateTime("2024-08-01T00:00:00-03:00");
        Events events = service.events().list(id)
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    // Método que inicia o fluxo OAuth2
    public String initiateGoogleAuthFlow(StateDTO dto) throws Exception {
        CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(dto.getCompanyId())
                .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));

        String base64 = StateDTO.encodeToBase64(dto);
        // Obtenha o fluxo de autenticação OAuth2
        AuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(companyConfigEntity.getGoogleCredential());
        // Cria a URL de autorização
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri("http://localhost:8081/v1/google-calendar/callback")
                .setState(base64); // URL de callback

        // Retorna a URL de autorização
        return authorizationUrl.build();
    }

    private AuthorizationCodeFlow getGoogleAuthorizationCodeFlow(CompanyConfigEntity.GoogleCredentialDTO dto) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, dto.getClientId(), dto.getClientSecret(), Collections.singleton(CalendarScopes.CALENDAR))
                .setAccessType("offline")
                .build();
    }

    // Método que troca o authorizationCode por um Credential
    public Credential exchangeAndStoreCodeForCredential(String authorizationCode, String companyId) throws IOException, CbioException {

        CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));

        AuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(companyConfigEntity.getGoogleCredential());

        Credential credentialStored = getCredentialByCompanyId(companyConfigEntity.getCompanyId());

        // Faz a troca do código de autorização por credenciais
        TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                .setRedirectUri("http://localhost:8081/v1/google-calendar/callback") // Sua URL de callback
                .execute();

        Credential credential = flow.createAndStoreCredential(tokenResponse, companyId);


        boolean hasNotCredentialSavedOrExpired = Boolean.FALSE.equals(companyService.hasGoogleCrendential(companyId)) ||
                getMiliResult(Objects.requireNonNull(credentialStored)) < 0;

        if (hasNotCredentialSavedOrExpired) {

            GoogleCredentialEntity googleCredentialStored = googleCredentialRepository.findByUserId(companyConfigEntity.getCompanyId())
                    .orElse(GoogleCredentialEntity.builder().build());

            CredentialData credentialData = CredentialData.builder()
                    .accessToken(credential.getAccessToken())
                    .refreshToken(credential.getRefreshToken())
                    .expirationTimeMillis(credential.getExpirationTimeMilliseconds())
                    .jsonFactory(credential.getJsonFactory())
                    .build();

            GoogleCredentialEntity googleCredentialEntity = GoogleCredentialEntity
                    .builder()
                    .id(googleCredentialStored.getId() != null ? googleCredentialStored.getId() : null)
                    .userId(companyId)
                    .credential(credentialData)
                    .createdTime(LocalDateTime.now())
                    .build();

            googleCredentialRepository.save(googleCredentialEntity);
        }

        syncCalendarWithResources(credential, companyConfigEntity.getCompanyId());

        return credential;
    }

    private static long getMiliResult(Credential credentialStore) {
        Instant instant = Instant.now();
        return credentialStore.getExpirationTimeMilliseconds() - instant.toEpochMilli();
    }

    @Override
    public void updateEvent(EventDTO dto, String eventId) throws CbioException, IOException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            Credential credential = getCredentialByCompanyId(companyIdUserLogged);

            ResourceDTO resourceByCompanyAndDairyName = resourceService.getResourceByCompanyAndDairyName(dto.getDairyName())
                    .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));;

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Lista todos os calendários
            CalendarListEntry calendar = getCalendar(dto.getDairyName(), service);

            Event event = service.events().get(calendar.getId(), eventId).execute();
            populateEvents(dto, resourceByCompanyAndDairyName, event);

            Event updatedEvent = service.events().update(calendar.getId(), eventId, event).execute();

            EventEntity entity = eventRepository.findById(eventId)
                    .orElseThrow(() -> new CbioException("Evento não encontrado.", HttpStatus.NO_CONTENT.value()));

            eventMapper.fromDto(dto, entity);
            eventRepository.save(entity);

            log.info("Evento atualizado: {}", updatedEvent.getHtmlLink());
        }
    }

    @Override
    public void notifyEvent(EventDTO dto) throws CbioException, IOException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            eventService.notify(dto.getId());
        }
    }

    public void insertEvent(EventDTO dto) throws CbioException, IOException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            Credential credential = getCredentialByCompanyId(companyIdUserLogged);

            ResourceDTO resourceByCompanyAndDairyName = resourceService.getResourceByCompanyAndDairyName(dto.getDairyName())
                    .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));;

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Lista todos os calendários
            CalendarListEntry calendar = getCalendar(dto.getDairyName(), service);

            // Criação de um evento
            Event events = new Event();
            populateEvents(dto, resourceByCompanyAndDairyName, events);

//            // Opções de recorrência
//            String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};  // Evento diário, ocorrendo por dois dias
//            events.setRecurrence(Arrays.asList(recurrence));

            // Participantes do evento
            List<EventAttendee> attendees = new ArrayList<>();
            attendees.add(new EventAttendee().setEmail(resourceByCompanyAndDairyName.getEmail()));

            if (StringUtils.hasText(dto.getEmail())) {
                attendees.add(new EventAttendee().setEmail(dto.getEmail()));
            }

            events.setAttendees(attendees);

            // Notificações via popup e e-mail
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(
                            new EventReminder().setMethod("email").setMinutes(24 * 60), // 1 dia antes
                            new EventReminder().setMethod("popup").setMinutes(10)       // 10 minutos antes
                    ));
            events.setReminders(reminders);

            events = service.events().insert(calendar.getId(), events).execute();

            ExtractedTimeZoneDateEvent result = getExtractedTimeZoneDateEvent(dto);

            EventEntity entity = EventEntity.builder()
                    .startDate(LocalDateTime.parse(result.startStr()))
                    .endDate(LocalDateTime.parse(result.endStr()))
                    .title(dto.getTitle())
                    .phone(dto.getPhone().replaceAll("\\D", ""))
                    .email(dto.getEmail())
                    .name(dto.getName())
                    .company(dto.getCompany())
                    .id(events.getId())
                    .className(dto.getClassName())
                    .email(dto.getEmail())
                    .description(dto.getDescription())
                    .dairyName(dto.getDairyName())
                    .notified(Boolean.FALSE)
                    .build();
            eventRepository.save(entity);

            System.out.printf("Evento criado: %s\n", events.getHtmlLink());
        }
    }

    @NotNull
    private static ExtractedTimeZoneDateEvent getExtractedTimeZoneDateEvent(EventDTO dto) {
        String startStr = dto.getStart().replaceAll("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}).*", "$1");
        String endStr = dto.getEnd().replaceAll("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}).*", "$1");
        ExtractedTimeZoneDateEvent result = new ExtractedTimeZoneDateEvent(startStr, endStr);
        return result;
    }

    private record ExtractedTimeZoneDateEvent(String startStr, String endStr) {
    }

    private static void populateEvents(EventDTO dto, ResourceDTO resourceByCompanyAndDairyName, Event events) {
        String title = StringUtils.hasText(dto.getTitle()) ? dto.getTitle() : resourceByCompanyAndDairyName.getTitle();
        String location = resourceByCompanyAndDairyName.getLocation();
        String description = StringUtils.hasText(dto.getDescription()) ? dto.getDescription() : resourceByCompanyAndDairyName.getDescription();
        events.setSummary(title);

        events.setLocation(location);

        events.setDescription(description);

        DateTime startDateTime = new DateTime(dto.getStart());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Sao_Paulo");
        events.setStart(start);

        DateTime endDateTime = new DateTime(dto.getEnd());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Sao_Paulo");
        events.setEnd(end);
    }

    public void delete(EventDTO dto, String eventId) throws CbioException, IOException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            Credential credential = getCredentialByCompanyId(companyIdUserLogged);


            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            // Lista todos os calendários

            CalendarListEntry calendar = getCalendar(dto.getDairyName(), service);

            eventRepository.deleteById(eventId);

            service.events().delete(calendar.getId(), eventId).execute();
        }
    }

    // Exemplo de método para listar eventos usando as credenciais
    public List<Event> listEvents(String id, String company) throws IOException, CbioException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {

            Credential credential = getCredentialByCompanyId(companyIdUserLogged);

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            // Lista todos os calendários
            CalendarListEntry calendar = getCalendar(id, service);

            DateTime now = new DateTime("2024-08-01T00:00:00-03:00");
            Events events = service.events().list(calendar.getId())
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            if (items.isEmpty()) {
                System.out.println("No upcoming events found.");
            } else {
                System.out.println("Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    System.out.printf("%s (%s)\n", event.getSummary(), start);
                }
            }
            return items;
        } else {
            return null;
        }

    }

    @Override
    public List<EventDTO> listEventsByResource(String id, QueryFilterCalendarDTO dateDTO) throws CbioException, IOException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            ResourceDTO resourceById = resourceService.getResourceById(id);

            if(ResourceEntity.StatusEnum.DESYNC.equals(resourceById.getStatus())){
                throw new RuntimeException(String.format("Recurso '%s' está desconfigurado. Configure através do menu 'Recursos'.", resourceById.getDairyName()));
            }


            Credential credential = getCredentialByCompanyId(companyIdUserLogged);

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            CalendarListEntry calendar = getCalendar(resourceById.getDairyName(), service);

            DateTime date = new DateTime(dateDTO.getStartStr());
            Events listEvents = service.events().list(calendar.getId())
                    .setMaxResults(10)
                    .setTimeMin(date)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            // Lista todos os cale
            List<EventDTO> collect = listEvents.getItems()
                    .stream().map(
                            event -> {
                                Optional<EventEntity> byId = eventRepository.findById(event.getId());
                                EventEntity entity;
                                if (byId.isEmpty()) {
                                    entity = saveAndGetEvent(event, resourceById);
                                } else {
                                    entity = byId.get();
                                }
                                return EventDTO.builder()
                                        .id(event.getId())
                                        .description(event.getDescription())
                                        .title(event.getSummary())
                                        .end(getDateTimeFormated(event.getEnd()))
                                        .start(getDateTimeFormated(event.getStart()))
                                        .emailTo(getEventAttendee(event))
                                        .dairyName(resourceById.getDairyName())
                                        .className(resourceById.getColor().getClassColor())
                                        .phone(entity.getPhone())
                                        .name(entity.getName())
                                        .email(entity.getEmail())
                                        .build();
                            }

                    )
                    .collect(Collectors.toList());
            return collect;
        }
        return List.of();
    }

    private EventEntity saveAndGetEvent(Event event, ResourceDTO resourceById) {
        String dateTimeFormatedStart = getDateTimeFormated(event.getStart());
        LocalDateTime startDate = LocalDateTime.parse(dateTimeFormatedStart);
        String dateTimeFormatedEnd = getDateTimeFormated(event.getEnd());
        LocalDateTime endDate = LocalDateTime.parse(dateTimeFormatedEnd);


        EventDTO dto = EventDTO.builder()
                .id(event.getId())
                .description(event.getDescription())
                .title(event.getSummary())
                .startDate(startDate)
                .endDate(endDate)
                .emailTo(getEventAttendee(event))
                .dairyName(resourceById.getDairyName())
                .className((resourceById.getColor() != null) ? resourceById.getColor().getClassColor() : "primary")
                .notified(Boolean.FALSE)
                .build();

        return eventRepository.save(eventMapper.toEntity(dto));
    }

    private static String getDateTimeFormated(EventDateTime event) {
        if (!ObjectUtils.isEmpty(event) && !ObjectUtils.isEmpty(event.get("dateTime"))) {

            String dateTime = event.get("dateTime").toString();
            int lastDashIndex = dateTime.lastIndexOf('-');
            String dateWithoutOffset = dateTime.substring(0, lastDashIndex);
            return dateWithoutOffset.split("\\.")[0];

        } else if(!ObjectUtils.isEmpty(event) && !ObjectUtils.isEmpty(event.get("date"))) {

            return event.get("date").toString().concat("T00:00:00");
        }else{
            return "";
        }
    }

    private String getEventAttendee(Event event) {
        if (!CollectionUtils.isEmpty(event.getAttendees())) {
            return event.getAttendees().get(0).get("email").toString();
        } else {
            return null;
        }
    }


    private static List<CalendarListEntry> getListCalendar(Calendar service) throws IOException {
        Calendar.CalendarList.List request = service.calendarList().list();
        CalendarList calendarList = request.execute();

        return calendarList.getItems();
    }

    private void syncCalendarWithResources(Credential credential, String companyId) throws IOException {

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<CalendarListEntry> listCalendar = getListCalendar(service);
        listCalendar
                .forEach(calendarListEntry -> {

                    Optional<ResourceDTO> resourceByCompanyAndDairyName = resourceService.getResourceByCompanyAndDairyNameAndCompanyId(calendarListEntry.getSummary(), companyId);
                    if(resourceByCompanyAndDairyName.isEmpty()){
                        resourceService.saveResourceByDairyName(calendarListEntry.getSummary(), companyId);
                    }

                });

    }

    private static CalendarListEntry getCalendar(String calendarName, Calendar service) throws IOException {

        List<CalendarListEntry> items = getListCalendar(service);

        Optional<CalendarListEntry> calendarOptional = items.stream()
                .filter(calendarListEntry -> calendarListEntry.getSummary().equals(calendarName))
                .findFirst();
        CalendarListEntry calendar = calendarOptional.orElseThrow(() -> new NotFoundException("Calendário não encontrado"));
        return calendar;
    }

    private Credential getCredentialByCompanyId(String companyIdUserLogged) throws CbioException {
        Optional<GoogleCredentialEntity> optByUserId = googleCredentialRepository.findByUserId(companyIdUserLogged);
        if (optByUserId.isPresent()) {
            GoogleCredentialEntity googleCredentialEntity = optByUserId.get();
            CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(companyIdUserLogged)
                    .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));

            return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(HTTP_TRANSPORT)  // transporte HTTP
                    .setJsonFactory(JSON_FACTORY)  // JSON factory
                    .setClientAuthentication(
                            new ClientParametersAuthentication(
                                    companyConfigEntity.getGoogleCredential().getClientId(),
                                    companyConfigEntity.getGoogleCredential().getClientSecret()
                            ))  // autenticação do cliente
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))  // URL do servidor de tokens
                    .build()
                    .setAccessToken(googleCredentialEntity.getCredential().getAccessToken())  // token de acesso
                    .setRefreshToken(googleCredentialEntity.getCredential().getRefreshToken())  // token de refresh
                    .setExpirationTimeMilliseconds(googleCredentialEntity.getCredential().getExpirationTimeMillis());
        } else {
            return null;
        }

    }
}
