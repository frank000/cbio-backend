package com.cbio.core.v1.dto;

import com.cbio.app.base.repository.FiltroDTOInterface;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class AttendantFiltroGridDTO implements Serializable, FiltroDTOInterface {

    private String busca;
    private String idCompany;
    private List<String> perfil;


}
