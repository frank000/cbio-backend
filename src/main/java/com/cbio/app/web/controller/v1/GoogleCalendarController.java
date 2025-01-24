package com.cbio.app.web.controller.v1;

import com.cbio.app.exception.CbioException;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.google.QueryFilterCalendarDTO;
import com.cbio.core.v1.dto.google.ScheduleDTO;
import com.cbio.core.v1.dto.google.StateDTO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarController implements SecuredRestController {

    private final CalendarGoogleService calendarGoogleService;
    private final AuthService authService;

    @Value("${app.external-front-url}")
    private String externalUrl;

    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> authorizeGoogleCalendar(
            @RequestParam(value = "companyMail", required = false) String companyMail,
            HttpServletRequest request) throws Exception {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        if (StringUtils.hasText(companyIdUserLogged)) {

            StateDTO build = new StateDTO(companyMail, companyIdUserLogged, request.getRequestURL().toString());


            String authorizationUrl = calendarGoogleService.initiateGoogleAuthFlow(build);
            Map<String, String> response = new HashMap<>();
            response.put("url", authorizationUrl);

            return ResponseEntity
                    .ok()
                    .body(response);
        } else {
            return null;
        }
    }

    @GetMapping("/callback")
    public void handleGoogleCallback(
            @RequestParam("code") String authorizationCode,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws Exception {

        StateDTO stateDTO = StateDTO.decodeFromBase64(state);
        Credential credential = calendarGoogleService.exchangeAndStoreCodeForCredential(authorizationCode, stateDTO.getCompanyId());
        if (credential != null) {


            calendarGoogleService.executa(stateDTO.getCompanyMail(), credential);
            response.sendRedirect(String.format("%s/apps/agendai/calendar", externalUrl));

        }
    }

    @GetMapping("/list")
    public void list(@RequestParam("email") String email, @RequestParam(value = "company", required = false) String company) throws IOException, CbioException {

        List<Event> events = calendarGoogleService.listEvents(email, company);
    }

    @PostMapping("/events-by-resource")
    public ResponseEntity<List<EventDTO>> eventsByResource(
            @RequestBody QueryFilterCalendarDTO queryDTO) throws CbioException, IOException {
        return ResponseEntity.ok(calendarGoogleService.listEventsByResource(queryDTO.getResourceId(), queryDTO));
    }

    @PostMapping("/schedule-by-resource")
    public ResponseEntity<Map<String, List<ScheduleDTO>>> scheduleByResource(
            @RequestBody QueryFilterCalendarDTO queryDTO) throws CbioException, IOException {
        return ResponseEntity.ok(calendarGoogleService.listScheduleByResource(queryDTO.getResourceId(), queryDTO));
    }

    @PostMapping("/event")
    public ResponseEntity<Void> insertEvent(
            @RequestBody EventDTO dto) throws CbioException, IOException {
        calendarGoogleService.insertEvent(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event/notify")
    public ResponseEntity<Void> notifyEvent(
            @RequestBody EventDTO dto) throws CbioException, IOException {
        calendarGoogleService.notifyEvent(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/event/{eventId}")
    public ResponseEntity<Void> insertEvent(
            @PathVariable String eventId,
            @RequestBody EventDTO dto) throws CbioException, IOException {
        calendarGoogleService.updateEvent(dto, eventId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-event/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @RequestBody EventDTO dto,
            @PathVariable String eventId) throws CbioException, IOException {
        calendarGoogleService.delete(dto, eventId);
        return ResponseEntity.ok().build();
    }
}
