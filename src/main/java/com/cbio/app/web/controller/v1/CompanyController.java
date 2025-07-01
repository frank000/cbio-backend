package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.StatusPaymentEnum;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.grid.CompanyGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.v1.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/company")
public class CompanyController implements SecuredRestController {

    private final CompanyService companyService;
    private final CompanyGridRepository companyGridRepository;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<CompanyDTO> save(@RequestBody CompanyDTO companyDTO) throws CbioException {

        CompanyDTO save = companyService.save(companyDTO);

        return ResponseEntity.ok(save);
    }

    @PutMapping
    public ResponseEntity<CompanyDTO> edit(@RequestBody CompanyDTO companyDTO) {

        CompanyDTO save = companyService.edit(companyDTO);

        return ResponseEntity.ok(save);
    }


    @GetMapping("{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable String id) {

        return ResponseEntity
                .ok( companyService.findById(id));
    }

    @GetMapping("/credential-instagram/{id}")
    public ResponseEntity<InstagramCredentialDTO> getCredential(@PathVariable String id) {

        return ResponseEntity
                .ok( companyService.getCredentialInstagram(id));
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

    @GetMapping("/free-port")
    public ResponseEntity<Integer> getFree() {

        return ResponseEntity
                .ok( companyService.getFreePort());
    }

    @PostMapping("/config")
    public ResponseEntity<CompanyConfigDTO> saveConfig(@RequestBody CompanyConfigDTO dto) throws CbioException, IOException, InterruptedException {

        CompanyConfigDTO save = companyService.saveConfigCompany(dto);

        return ResponseEntity.ok(save);
    }

    @PutMapping("/config")
    public ResponseEntity<CompanyConfigDTO> updateConfig(@RequestBody CompanyConfigDTO dto) throws CbioException, IOException, InterruptedException {

        CompanyConfigDTO save = companyService.saveConfigCompany(dto);

        return ResponseEntity.ok(save);
    }

    @PutMapping("/scheduler")
    public ResponseEntity<CompanyConfigDTO> schedulerConfig() throws IOException {

        companyService.toggleSchedulingToCompany();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/scheduler")
    public ResponseEntity<Boolean> isShedulerConfig() throws IOException {

        return ResponseEntity.ok(companyService.isCompanyScheduler());
    }


    @GetMapping("/config")
    public ResponseEntity<CompanyConfigDTO> getConfig() throws CbioException {

        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if(StringUtils.hasText(companyIdUserLogged)){
            CompanyConfigDTO save = companyService.getConfigCompany(companyIdUserLogged);

            return ResponseEntity.ok(save);
        }else{
            return null;
        }
    }
    @GetMapping("/config-preferences")
    public ResponseEntity<CompanyConfigDTO> getPreferences() throws CbioException {

        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if(StringUtils.hasText(companyIdUserLogged)){
            CompanyConfigDTO save = companyService.fetchOrCreateConfigPreferencesCompany(companyIdUserLogged);
            return ResponseEntity.ok(save);
        }else{
            return null;
        }
    }

    @GetMapping("/credential/has")
    public ResponseEntity<Boolean> hasCredential() {
        return ResponseEntity.ok(companyService.hasGoogleCrendential());
    }


    @GetMapping("/status-payment")
    public ResponseEntity<StatusPaymentEnum> statusPayment() {
        return ResponseEntity.ok(companyService.getStatusPayment());
    }

    @GetMapping("/status-payment/list")
    public ResponseEntity<List<ItemSelecaoDTO>> statusPaymentList() {
        List<ItemSelecaoDTO> list = Arrays.stream(StatusPaymentEnum.values()).toList()
                .stream().map(statusPaymentEnum -> {
                    return ItemSelecaoDTO.builder()
                            .id(statusPaymentEnum.name())
                            .label(statusPaymentEnum.name())
                            .build();
                }).toList();
        return ResponseEntity.ok(list);
    }


}
