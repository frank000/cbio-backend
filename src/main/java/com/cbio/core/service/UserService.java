package com.cbio.core.service;

import com.cbio.core.v1.dto.UsuarioDTO;

public interface UserService {

    void salva(UsuarioDTO usuarioDTO, String password);

    UsuarioDTO buscaPorId(String id);

    UsuarioDTO fetch();
}
