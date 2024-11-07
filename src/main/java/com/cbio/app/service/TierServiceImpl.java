package com.cbio.app.service;

import com.cbio.app.entities.TierEntity;
import com.cbio.app.repository.TierRepository;
import com.cbio.app.service.mapper.TierMapper;
import com.cbio.core.service.TierService;
import com.cbio.core.v1.dto.TierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TierServiceImpl implements TierService {

    private final TierRepository tierRepository;
    private final TierMapper tierMapper;

    @Override
    public TierDTO salva(TierDTO tierDTO) {
        TierEntity entity = tierMapper.toEntity(tierDTO);
        return tierMapper.toDto(tierRepository.save(entity));
    }

    @Override
    public List<TierDTO> listAll() {
        return tierMapper.toDto(tierRepository.findAll());
    }
}
