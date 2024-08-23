package com.cbio.app.service;

import com.cbio.app.entities.AttendantEntity;
import com.cbio.app.repository.AttendantRepository;
import com.cbio.app.service.mapper.AttendantMapper;
import com.cbio.core.service.AttendantService;
import com.cbio.core.v1.dto.AttendantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttendantServiceImpl implements AttendantService {

    private final AttendantRepository attendantRepository;
    private final AttendantMapper attendantMapper;


    @Override
    public void salva(AttendantDTO attendantDTO) {

        attendantRepository.save(attendantMapper.toEntity(attendantDTO));
    }

    @Override
    public AttendantDTO buscaPorId(String id) {
        AttendantEntity attendantEntity = attendantRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Atendente não encontrado."));

        return attendantMapper.toDto(attendantEntity);
    }

    @Override
    public AttendantDTO fetch() {
        AttendantEntity attendantEntity = attendantRepository.findAll().stream().findAny()
                .orElseThrow(() -> new RuntimeException("Nenhum Atendentente encontrado."));
        return attendantMapper.toDto(attendantEntity);
    }
}
