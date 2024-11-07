package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryDTO implements Serializable {


    private String id;
    private String name;
    private String perfil;
    private Boolean active;
    private String stringId;
    private String mesNumero;
    private String mes;
    private String initCanal;
    private Object channels;
    private Object channelsFiltered;
    private Integer channelsSize;
    private Integer qntAttendances;
}
