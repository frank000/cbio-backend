package com.cbio.app.web.controller.v1;

import com.cbio.core.service.AttendantService;
import com.cbio.core.v1.dto.AttendantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/atendente")
public class AttendantController {

    private final AttendantService attendantService;

    @PostMapping
    public ResponseEntity<Void> salva(@RequestBody AttendantDTO attendantDTO) {

        attendantService.salva(attendantDTO);

        return ResponseEntity.ok().build();
    }
    @GetMapping("{id}")
    public ResponseEntity<AttendantDTO> salva(@PathVariable String id) {

        return ResponseEntity
                .ok( attendantService.buscaPorId(id));
    }
}
