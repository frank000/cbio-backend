package com.cbio.app.service;

import com.cbio.app.entities.PhraseEntity;
import com.cbio.app.repository.PhraseRepository;
import com.cbio.app.service.mapper.PhraseMapper;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.PhraseService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.PhraseDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PhraseServiceImpl implements PhraseService {

    private final PhraseRepository phraseRepository;
    private final PhraseMapper phraseMapper;
    private final AuthService authService;

    @Override
    public PhraseDTO save(PhraseDTO attendantDTO) {

        if(authService.getClaimsUserLogged().get("companyId") != null){
            String companyId = authService.getClaimsUserLogged().get("companyId").toString();

            attendantDTO.setCompany(CompanyDTO.builder()
                    .id(companyId)
                    .build());

            PhraseEntity entity = phraseMapper.toEntity(attendantDTO);
            return phraseMapper.toDto(phraseRepository.save(entity));

        }else{

            throw new RuntimeException("Você não está logado");
        }


    }

    @Override
    public PhraseDTO update(PhraseDTO phraseDTO) {
        PhraseEntity phraseEntity = phraseRepository.findById(phraseDTO.getId()).orElseThrow();
        phraseMapper.fromDto(phraseDTO, phraseEntity);

        return phraseMapper.toDto(phraseRepository.save(phraseEntity));
    }

    @Override
    public PhraseDTO getById(String id) {
        PhraseEntity phraseEntity = phraseRepository.findById(id).orElseThrow(() -> new RuntimeException("Frase não encontrada."));
        return phraseMapper.toDto(phraseEntity);
    }

    @Override
    public void delete(String id) {
        phraseRepository.findById(id).ifPresent(phraseRepository::delete);
    }

    @Override
    public List<PhraseDTO> fetch() {
        String companyId = authService.getClaimsUserLogged().get("companyId").toString();
        if(ObjectUtils.isNotEmpty(companyId)){
            List<PhraseEntity> all = phraseRepository.findAllByCompanyId(companyId);
            return phraseMapper.toDto(all);
        }else{
            return null;
        }

    }
}
