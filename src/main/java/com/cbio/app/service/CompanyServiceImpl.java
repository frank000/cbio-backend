package com.cbio.app.service;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.CompanyRepository;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.CompanyMapper;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.IAMService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.UserKeycloak;
import com.cbio.core.v1.dto.UsuarioDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyDTO save(CompanyDTO companyDTO) {
        CompanyEntity entity = companyMapper.toEntity(companyDTO);
        CompanyEntity save = companyRepository.save(entity);

        return companyMapper.toDto(save);
    }

    public CompanyDTO findById(String id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Companhia não encontrada"));
        return companyMapper.toDto(entity);
    }

}
