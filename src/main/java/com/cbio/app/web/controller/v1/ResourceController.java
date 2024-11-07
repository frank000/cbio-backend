package com.cbio.app.web.controller.v1;

import com.cbio.app.base.grid.PageableResponse;
import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.repository.grid.ResourceGridRepository;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.ResourceService;
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
@RequestMapping("/v1/resource")
@RequiredArgsConstructor
public class ResourceController implements SecuredRestController {

    private final AuthService authService;
    private final ResourceGridRepository resourceGridRepository;
    private final ResourceService resourceService;

    @GetMapping("/test")
    public void teste(){
        resourceService.notifyByConfigNotification();
    }

    @PostMapping
    public ResourceDTO save(@RequestBody ResourceDTO dto) {
        return resourceService.save(dto);
    }

    @PutMapping
    public ResourceDTO udpate(@RequestBody ResourceDTO dto) {
        return resourceService.update(dto);
    }

    @GetMapping("/{id}")
    public ResourceDTO getById(@PathVariable String id) {
        return resourceService.getResourceById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        resourceService.delete(id);

    }

    @GetMapping("grid")
    ResponseEntity<PageableResponse<ResourceDTO>> obtemGrid(@RequestParam(required = false) final String filter,
                                                            @RequestParam(required = false) final String companyId,
                                                            @RequestParam(defaultValue = "0") Integer pageIndex,
                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                            @RequestParam(defaultValue = "id") String sortField,
                                                            @RequestParam(defaultValue = "DESC") String sortType) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.fromString(sortType), sortField);

        ResourceFiltroGridDTO produtoGridFiltroDTO = ResourceFiltroGridDTO.builder()
                .busca(filter)
                .idCompany(ObjectUtils.isEmpty(companyId) ? authService.getCompanyIdUserLogged() : companyId)
                .build();
        PageableResponse<ResourceDTO> all = resourceGridRepository.obtemGrid(produtoGridFiltroDTO, pageable, ResourceEntity.class, ResourceDTO.class);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/list-resource-filter")
    public ResponseEntity<List<ResourceDTO>> getResourceFilter() {
        return ResponseEntity.ok(resourceService.getResourceFilterSelection());
    }
}
