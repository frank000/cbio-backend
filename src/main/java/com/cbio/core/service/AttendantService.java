package com.cbio.core.service;

import com.cbio.core.v1.dto.AttendantDTO;

public interface AttendantService  {

    void salva(AttendantDTO attendantDTO);
    AttendantDTO buscaPorId(String id);

    AttendantDTO fetch();
}
