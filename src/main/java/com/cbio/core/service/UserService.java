package com.cbio.core.service;

import com.cbio.core.v1.dto.UsuarioDTO;

public interface UserService {

    UsuarioDTO salva(UsuarioDTO usuarioDTO, String password, String role);

    UsuarioDTO buscaPorId(String id);

    UsuarioDTO fetch();
}
