package com.cbio.core.v1.dto;

import com.cbio.app.base.repository.FiltroDTOInterface;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class ContactFiltroGridDTO implements Serializable, FiltroDTOInterface {

    private String busca;
    private String idCompany;


}
