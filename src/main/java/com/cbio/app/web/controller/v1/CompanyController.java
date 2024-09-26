package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.repository.grid.CompanyGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.CompanyService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.CompanyFiltroGridDTO;
import com.cbio.core.v1.dto.CompanyGridDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/company")
public class CompanyController implements SecuredRestController {

    private final CompanyService companyService;
    private final CompanyGridRepository companyGridRepository;

    @PostMapping
    public ResponseEntity<CompanyDTO> save(@RequestBody CompanyDTO companyDTO) {

        CompanyDTO save = companyService.save(companyDTO);

        return ResponseEntity.ok(save);
    }


    @GetMapping("{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable String id) {

        return ResponseEntity
                .ok( companyService.findById(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        companyService.delete(id);
        return ResponseEntity
                .ok().build();
    }


    @GetMapping("grid")
    public ResponseEntity<PageableResponse<CompanyGridDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                      @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                                      @RequestParam(defaultValue = "dataHoraCriacao") String sortField,
                                                                      @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        CompanyFiltroGridDTO produtoGridFiltroDTO = CompanyFiltroGridDTO.builder()
                .busca(filter)
                .build();
        PageableResponse<CompanyGridDTO> companyGridDTOPageableResponse = companyGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, CompanyEntity.class, CompanyGridDTO.class);
        return ResponseEntity.ok(companyGridDTOPageableResponse);
    }


}
