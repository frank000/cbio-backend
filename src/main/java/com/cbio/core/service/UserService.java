package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.UsuarioDTO;

public interface UserService {

    UsuarioDTO salva(UsuarioDTO usuarioDTO, String password, String role);

    UsuarioDTO update(UsuarioDTO usuarioDTO, String password, String role);

    UsuarioDTO buscaPorId(String id);

    UsuarioDTO adminByCompany(String id);

    UsuarioDTO fetch();

    void updatePassword(UsuarioDTO usuarioDTO, String password) throws CbioException;

    void update(String id, UsuarioDTO.UsuarioFormDTO usuarioDTO);
}
