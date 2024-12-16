package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.TicketEntity;
import com.cbio.app.repository.grid.PhraseGridRepository;
import com.cbio.app.repository.grid.TicketGridRepository;
import com.cbio.app.service.enuns.TicketsTypeEnum;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.TicketService;
import com.cbio.core.v1.dto.TicketDTO;
import com.cbio.core.v1.dto.TicketsFiltroGridDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/ticket")
@RequiredArgsConstructor
public class TicketController implements SecuredRestController {

    private final TicketService ticketService;
    private final TicketGridRepository ticketGridRepository;
    private final AuthService authService;

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        ticketService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TicketDTO> getById(@PathVariable("id") String id) {

        return ResponseEntity.ok(ticketService.getById(id));
    }

    @GetMapping(value = "/tipos")
    public ResponseEntity<List<TicketsTypeEnum>> getTypes() {

        return ResponseEntity.ok(Arrays.stream(TicketsTypeEnum.values()).toList());
    }


    @PostMapping
    public ResponseEntity<TicketDTO> save(@RequestBody TicketDTO dto) {

        return ResponseEntity.ok(ticketService.save(dto));
    }

    @PutMapping
    public ResponseEntity<TicketDTO> updtade(@RequestBody TicketDTO dto) {

        return ResponseEntity.ok(ticketService.update(dto));
    }

    @GetMapping("grid")
    public ResponseEntity<PageableResponse<TicketDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                                 @RequestParam(required = false) final String companyId,
                                                                 @RequestParam(defaultValue = "0") Integer pageIndex,
                                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                                 @RequestParam(defaultValue = "id") String sortField,
                                                                 @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);


        TicketsFiltroGridDTO produtoGridFiltroDTO = new TicketsFiltroGridDTO();
        produtoGridFiltroDTO.setBusca(filter);
        produtoGridFiltroDTO.setIdCompany(ObjectUtils.isEmpty(companyId)?authService.getCompanyIdUserLogged() : companyId);


        PageableResponse<TicketDTO> all = ticketGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, TicketEntity.class, TicketDTO.class);
        return ResponseEntity.ok(all);
    }

}

