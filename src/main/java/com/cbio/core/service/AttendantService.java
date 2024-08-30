package com.cbio.core.service;

import com.cbio.core.v1.dto.UsuarioDTO;

public interface AttendantService  {

    void salva(UsuarioDTO attendantDTO);

    UsuarioDTO buscaPorId(String id);

    UsuarioDTO fetch();
}
