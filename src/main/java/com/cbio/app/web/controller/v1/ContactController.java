package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.ContactEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.grid.ContactsGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.ContactService;
import com.cbio.core.v1.dto.ContactDTO;
import com.cbio.core.v1.dto.ContactFiltroGridDTO;
import io.minio.errors.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/contact")
public record ContactController(ContactService service,
                                ContactsGridRepository contactGridRepository,
                                AuthService authService
                                ) implements SecuredRestController {

    @PostMapping
    private ResponseEntity<ContactDTO> save(@RequestBody ContactDTO dto) throws CbioException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        dto.setAppCreated(Boolean.FALSE);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PutMapping
    private ResponseEntity<ContactDTO> update(@RequestBody ContactDTO dto) throws CbioException {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.update(dto));
    }

    @GetMapping
    private ResponseEntity<List<ContactDTO>> list() throws CbioException {
        return ResponseEntity.status(HttpStatus.OK).body(service.getContacts());
    }

    @GetMapping(value = "/{id}")
    private ResponseEntity<ContactDTO> obtem(@PathVariable String id) throws CbioException {

        return ResponseEntity.status(HttpStatus.OK).body(service.getContact(id));
    }

    @DeleteMapping(value = "/{id}")
    private ResponseEntity<Void> delete(@PathVariable String id) throws CbioException {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("grid")
    public ResponseEntity<PageableResponse<ContactDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                      @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                                      @RequestParam(defaultValue = "dataHoraCriacao") String sortField,
                                                                      @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        ContactFiltroGridDTO dto  = ContactFiltroGridDTO.builder()
                .busca(filter)
                .idCompany(companyIdUserLogged)
                .build();
        PageableResponse<ContactDTO> companyGridDTOPageableResponse = contactGridRepository.obtemGrid(dto, pageable, ContactEntity.class, ContactDTO.class);
        return ResponseEntity.ok(companyGridDTOPageableResponse);
    }

}
