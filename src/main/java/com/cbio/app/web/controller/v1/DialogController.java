package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.DialogoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/dialog")
public class DialogController implements SecuredRestController {

    private final DialogoService dialogoService;

    @GetMapping("/sender/{identificadorRementente}")
    public ResponseEntity<List<DialogoDTO>> obtemGrid(@PathVariable String identificadorRementente) {
        String value = ObjectUtils.requireNonEmpty(identificadorRementente);
        List<DialogoDTO> allBySender = dialogoService.getAllBySender(value);

        return ResponseEntity.ok(allBySender);
    }
}