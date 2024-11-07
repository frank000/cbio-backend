package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.PhraseEntity;
import com.cbio.app.repository.grid.PhraseGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.PhraseService;
import com.cbio.core.v1.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/phrase")
@RequiredArgsConstructor
public class PhraseAutoController implements SecuredRestController {

    private final PhraseService phraseService;
    private final PhraseGridRepository phraseGridRepository;
    private final AuthService authService;

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {

        phraseService.delete(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<PhraseDTO> getById(@PathVariable("id") String id) {

        return ResponseEntity.ok(phraseService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PhraseDTO>> getAll( ) {

        return ResponseEntity.ok(phraseService.fetch());
    }

    @PostMapping
    public ResponseEntity<PhraseDTO> save(@RequestBody PhraseDTO phraseDTO ) {

        return ResponseEntity.ok(phraseService.save(phraseDTO));
    }

    @PutMapping
    public ResponseEntity<PhraseDTO> updtade( @RequestBody PhraseDTO phraseDTO) {

        return ResponseEntity.ok(phraseService.update(phraseDTO));
    }

    @GetMapping("grid")
    public ResponseEntity<PageableResponse<PhraseDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                 @RequestParam(required = false) final String companyId,
                                                                  @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                  @RequestParam(defaultValue = "id") String sortField,
                                                                  @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        PhraseFiltroGridDTO produtoGridFiltroDTO = PhraseFiltroGridDTO.builder()
                .busca(filter)
                .idCompany(ObjectUtils.isEmpty(companyId)?authService.getCompanyIdUserLogged() : companyId)
                .build();
        PageableResponse<PhraseDTO> all = phraseGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, PhraseEntity.class, PhraseDTO.class);
        return ResponseEntity.ok(all);
    }

}

