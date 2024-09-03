package com.cbio.core.service;

import com.cbio.core.v1.dto.UsuarioDTO;

public interface AttendantService {

    UsuarioDTO salva(UsuarioDTO.UsuarioFormDTO attendantDTO);

    UsuarioDTO altera(UsuarioDTO.UsuarioFormDTO attendantDTO);

    UsuarioDTO buscaPorId(String id);

    void delete(String id);

    UsuarioDTO fetch();
}
