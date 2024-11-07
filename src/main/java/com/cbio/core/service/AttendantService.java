package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.UsuarioDTO;

public interface AttendantService {

    UsuarioDTO salva(UsuarioDTO.UsuarioFormDTO attendantDTO) throws CbioException;

    UsuarioDTO altera(UsuarioDTO.UsuarioFormDTO attendantDTO);

    UsuarioDTO buscaPorId(String id);

    void delete(String id);

    UsuarioDTO fetch();

    UsuarioDTO findTopByOrderByTotalChatsDistribuidosAsc(String companyId);

    void incrementTotalChatsReceived(UsuarioDTO usuarioDTO);

    Boolean isAttendantActive(String id);
}
