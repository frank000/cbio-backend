package com.cbio.core.v1.dto;

import com.cbio.app.base.repository.FiltroDTOInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class TicketsFiltroGridDTO extends BaseFiltroDTO implements Serializable, FiltroDTOInterface {

    private String status;
    private String type;


}
