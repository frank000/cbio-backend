package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.CanalEntity;
import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.repository.grid.CanalConfigGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.CanalService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.CompanyFiltroGridDTO;
import com.cbio.core.v1.dto.CompanyGridDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/canal")
public record CanalController(CanalService service, CanalConfigGridRepository canalConfigGridRepository) implements SecuredRestController {



    @PostMapping
    public CanalDTO incluirCanal(@RequestBody CanalDTO canal) {
        return service.incluirCanal(canal);
    }

    @GetMapping(value = "/listar-todos-canais")
    List<CanalEntity> listTabCanal() {
        return service.listarTodos();
    }

    @GetMapping(value = "/{id}")
    CanalDTO obtem(@PathVariable String id) {
        return service.obtemPorId(id);
    }

    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable String id) {
          service.delete(id);
    }

    @PutMapping
    private void alterar(@RequestBody CanalDTO canal) throws Exception {

        service.alterar(canal);

    }
    @DeleteMapping(value = "/deleta/{id}")
    private void deleta( @Valid @PathVariable("id") String id) {

        service.deleta(id);

    }

    @GetMapping("grid")
    public ResponseEntity<PageableResponse<CanalDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                @RequestParam(required = false) final String idCompany,
                                                                      @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                                      @RequestParam(defaultValue = "dataHoraCriacao") String sortField,
                                                                      @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        CompanyFiltroGridDTO produtoGridFiltroDTO = CompanyFiltroGridDTO.builder()
                .busca(filter)
                .idCompany(idCompany)
                .build();
        PageableResponse<CanalDTO> canalDTOPageableResponse = canalConfigGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, CanalEntity.class, CanalDTO.class);
        return ResponseEntity.ok(canalDTOPageableResponse);
    }

}
