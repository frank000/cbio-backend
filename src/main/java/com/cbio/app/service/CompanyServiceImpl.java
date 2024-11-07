package com.cbio.app.service;

import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.GoogleCredentialEntity;
import com.cbio.app.entities.TierEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.CompanyConfigRepository;
import com.cbio.app.repository.CompanyRepository;
import com.cbio.app.repository.GoogleCredentialRepository;
import com.cbio.app.service.mapper.CompanyConfigMapper;
import com.cbio.app.service.mapper.CompanyMapper;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyConfigMapper companyConfigMapper;
    private final CompanyConfigRepository companyConfigRepository;
    private final GoogleCredentialRepository googleCredentialRepository;
    private final AuthService authService;

    public CompanyDTO save(CompanyDTO companyDTO) throws CbioException {
        CompanyEntity entity = companyMapper.toEntity(companyDTO);
        CompanyEntity save = companyRepository.save(entity);

        CompanyConfigDTO configDTO = CompanyConfigDTO.builder()
                .companyId(save.getId())
                .emailCalendar(save.getEmail())
                .build();

        saveConfigCompany(configDTO);

        return companyMapper.toDto(save);
    }

    public CompanyDTO findById(String id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Companhia não encontrada"));
        return companyMapper.toDto(entity);
    }

    @Override
    public void delete(String id) {
        companyRepository.findById(id).ifPresent(companyRepository::delete);
    }

    @Override
    public Integer getNumAttendantsToCompany(String id) {
        Optional<TierEntity> tierByCompanyId = companyRepository.getTierById(id);
        return tierByCompanyId
                .orElseThrow().getNumAttendants();
    }

    @Override
    public CompanyDTO edit(CompanyDTO companyDTO) {
        CompanyEntity companyEntity = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada."));

        companyMapper.fromDto(companyDTO, companyEntity);

        return companyMapper.toDto(companyRepository.save(companyEntity));
    }

    public Integer getFreePort() {
        CompanyEntity company = companyRepository.findFirstByOrderByDataCadastroAsc()
                .orElseThrow(() -> new NotFoundException("Nenhum encontrado"));

        return company.getPorta() + 1;
    }

    @Override
    public Integer getPortByIdCompany(String id) {
        return findById(id).getPorta();
    }

    public CompanyConfigDTO saveConfigCompany(CompanyConfigDTO dto) throws CbioException {
        CompanyConfigEntity entity;
        if(StringUtils.hasText(dto.getId())){
            entity = companyConfigRepository.findById(dto.getId())
                    .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));

            companyConfigMapper.fromDto(dto, entity);
            entity.setCompanyId(entity.getCompanyId());
        }else{
            entity = companyConfigMapper.toEntity(dto);

        }

        entity = companyConfigRepository.save(entity);
        return companyConfigMapper.toDto(entity);
    }

    @Override
    public CompanyConfigDTO getConfigCompany(String id) throws CbioException {
        CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(id)
                .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));
        return companyConfigMapper.toDto(companyConfigEntity);
    }

    public Boolean hasGoogleCrendential(String id){
        if(StringUtils.hasText(id)){
            Optional<GoogleCredentialEntity> byUserId = googleCredentialRepository.findByUserId(id);
            return byUserId.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public Boolean hasGoogleCrendential(){
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if(StringUtils.hasText(companyIdUserLogged)){
            Instant now = Instant.now();
            Optional<GoogleCredentialEntity> byUserId = googleCredentialRepository.findByUserId(companyIdUserLogged);
            return byUserId.isEmpty() || byUserId.get().getCredential().getExpirationTimeMillis() - now.toEpochMilli() < 0 ? Boolean.FALSE : Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
}