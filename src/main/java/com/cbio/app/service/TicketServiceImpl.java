package com.cbio.app.service;

import com.cbio.app.entities.TicketEntity;
import com.cbio.app.repository.TicketRepository;
import com.cbio.app.service.mapper.TicketMapper;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.TicketService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.TicketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final AuthService authService;

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;


    @Override
    public TicketDTO save(TicketDTO dto) {

        if(authService.getClaimsUserLogged().get("companyId") != null){
            String companyId = authService.getClaimsUserLogged().get("companyId").toString();

            dto.setCompany(CompanyDTO.builder()
                    .id(companyId)
                    .build());
            dto.setUserId(authService.getClaimsUserLogged().get("preferred_username").toString());
            TicketEntity entity = ticketMapper.toEntity(dto);
            return ticketMapper.toDto(ticketRepository.save(entity));

        }else{

            throw new RuntimeException("Você não está logado");
        }


    }

    @Override
    public TicketDTO update(TicketDTO dto) {
        TicketEntity ticketEntity = ticketRepository.findById(dto.getId()).orElseThrow();
        dto.setUserId(authService.getClaimsUserLogged().get("preferred_username").toString());
        
        ticketMapper.fromDto(dto, ticketEntity);

        return ticketMapper.toDto(ticketRepository.save(ticketEntity));
    }

    @Override
    public TicketDTO getById(String id) {
        TicketEntity ticketEntity = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Frase não encontrada."));
        return ticketMapper.toDto(ticketEntity);
    }

    @Override
    public void delete(String id) {
        ticketRepository.findById(id).ifPresent(ticketRepository::delete);
    }


}
