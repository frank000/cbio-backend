package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.TierService;
import com.cbio.core.v1.dto.TierDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/tier")
public record TierController(TierService tierService ) implements SecuredRestController {



    @PostMapping
    public TierDTO addTier(@RequestBody TierDTO tierDTO) {
        return tierService.salva(tierDTO);
    }

    @GetMapping
    List<TierDTO> listAll(){
        return tierService.listAll();
    }
//
//    @GetMapping(value = "/{id}")
//    CanalDTO obtem(@PathVariable String id) {
//        return service.obtemPorId(id);
//    }
//
//    @DeleteMapping(value = "/{id}")
//    void delete(@PathVariable String id) {
//          service.delete(id);
//    }
//
//    @PutMapping
//    private void alterar(@RequestBody CanalDTO canal) throws Exception {
//
//        service.alterar(canal);
//
//    }
//    @DeleteMapping(value = "/deleta/{id}")
//    private void deleta( @Valid @PathVariable("id") String id) {
//
//        service.deleta(id);
//
//    }
//
//    @GetMapping("grid")
//    public ResponseEntity<PageableResponse<CanalDTO>> obtemGrid(@RequestParam(required = false) final String filter,
//                                                                @RequestParam(required = false) final String idCompany,
//                                                                      @RequestParam(defaultValue = "0") Integer pageIndex,
//                                                                      @RequestParam(defaultValue = "10") Integer pageSize,
//                                                                      @RequestParam(defaultValue = "dataHoraCriacao") String sortField,
//                                                                      @RequestParam(defaultValue = "DESC") String sortType) {
//        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);
//
//        CompanyFiltroGridDTO produtoGridFiltroDTO = CompanyFiltroGridDTO.builder()
//                .busca(filter)
//                .idCompany(idCompany)
//                .build();
//        PageableResponse<CanalDTO> canalDTOPageableResponse = canalConfigGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, CanalEntity.class, CanalDTO.class);
//        return ResponseEntity.ok(canalDTOPageableResponse);
//    }

}
