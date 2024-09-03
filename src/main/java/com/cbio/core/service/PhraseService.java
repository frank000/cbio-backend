package com.cbio.core.service;

import com.cbio.core.v1.dto.PhraseDTO;

import java.util.List;

public interface PhraseService {

    PhraseDTO save(PhraseDTO attendantDTO);

    PhraseDTO update(PhraseDTO attendantDTO);

    PhraseDTO getById(String id);

    void delete(String id);

    List<PhraseDTO> fetch();
}
