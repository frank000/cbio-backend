package com.cbio.core.service;

import com.cbio.core.v1.dto.TicketDTO;

public interface TicketService {

    TicketDTO save(TicketDTO dto);

    TicketDTO update(TicketDTO dto);

    TicketDTO getById(String id);

    void delete(String id);

}
