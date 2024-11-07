package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.ModelEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.grid.TemplateGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.ModelService;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.ModelFiltroGridDTO;
import com.cbio.core.v1.dto.ModelGridDTO;
import com.cbio.core.v1.dto.SelecaoDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/template")
@RequiredArgsConstructor
public class TemplateController implements SecuredRestController {

    private final ModelService modelService;
    private final TemplateGridRepository templateGridRepository;
    private final AuthService authService;

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws CbioException {

        modelService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ModelDTO> getById(@PathVariable("id") String id) throws CbioException {

        return ResponseEntity.ok(modelService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ModelDTO>> getAll() {

        return ResponseEntity.ok(modelService.listAll());
    }

    @GetMapping("/selection")
    public ResponseEntity<List<SelecaoDTO>> getAllSelection() {

        return ResponseEntity.ok(modelService.listSelection());
    }

    @PostMapping
    public ResponseEntity<ModelDTO> save(@RequestBody ModelDTO dto) throws CbioException {

        return ResponseEntity.ok(modelService.save(dto));
    }

    @PutMapping
    public ResponseEntity<ModelDTO> updtade(@RequestBody ModelDTO dto) throws CbioException {

        return ResponseEntity.ok(modelService.update(dto));
    }

    @GetMapping("grid")
    public ResponseEntity<PageableResponse<ModelGridDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                    @RequestParam(required = false) final String companyId,
                                                                    @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                    @RequestParam(defaultValue = "id") String sortField,
                                                                    @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        ModelFiltroGridDTO produtoGridFiltroDTO = ModelFiltroGridDTO.builder()
                .busca(filter)
                .idCompany(ObjectUtils.isEmpty(companyId) ? authService.getCompanyIdUserLogged() : companyId)
                .build();
        PageableResponse<ModelGridDTO> all = templateGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, ModelEntity.class, ModelGridDTO.class);
        return ResponseEntity.ok(all);
    }

}

