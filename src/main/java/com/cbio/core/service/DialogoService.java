package com.cbio.core.service;

import com.cbio.chat.dto.DialogoDTO;

import java.util.List;

public interface DialogoService {

    DialogoDTO saveDialogo(DialogoDTO dialogo);

    DialogoDTO updateDialogo(DialogoDTO dialogo);

    DialogoDTO getById(String id);

    List<DialogoDTO> getAll();



}
