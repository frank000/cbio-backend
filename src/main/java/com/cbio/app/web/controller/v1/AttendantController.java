package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.AttendantEntity;
import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.grid.AttendantGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AttendantService;
import com.cbio.core.v1.dto.AttendantFiltroGridDTO;
import com.cbio.core.v1.dto.CompanyFiltroGridDTO;
import com.cbio.core.v1.dto.CompanyGridDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/attendant")
public class AttendantController implements SecuredRestController {

    private final AttendantGridRepository attendantGridRepository;
    private final AttendantService attendantService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> salva(@RequestBody UsuarioDTO.UsuarioFormDTO attendantDTO) {

        return ResponseEntity.ok(attendantService.salva(attendantDTO));
    }
    @PutMapping
    public ResponseEntity<UsuarioDTO> update(@RequestBody UsuarioDTO.UsuarioFormDTO attendantDTO) {

        return ResponseEntity.ok(attendantService.altera(attendantDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> salva(@PathVariable String id) {

        return ResponseEntity
                .ok( attendantService.buscaPorId(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        attendantService.delete(id);
        return ResponseEntity
                .ok().build();
    }


    @GetMapping("grid")
    public ResponseEntity<PageableResponse<UsuarioDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                      @RequestParam(required = false) final String perfil,
                                                                      @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                                      @RequestParam(defaultValue = "id") String sortField,
                                                                      @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        AttendantFiltroGridDTO produtoGridFiltroDTO = AttendantFiltroGridDTO.builder()
                .busca(filter)
                .perfil(List.of(perfil.split(",")))
                .build();
        PageableResponse<UsuarioDTO> usuarioDTOPageableResponse = attendantGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, UsuarioEntity.class, UsuarioDTO.class);
        return ResponseEntity.ok(usuarioDTOPageableResponse);
    }
}
