package com.cbio.core.service;

import com.cbio.core.v1.dto.TicketDTO;
import org.springframework.web.multipart.MultipartFile;

public interface TicketService {

    TicketDTO save(TicketDTO dto, MultipartFile image);

    TicketDTO update(TicketDTO dto, MultipartFile image);

    TicketDTO getById(String id);

    void delete(String id);

}
