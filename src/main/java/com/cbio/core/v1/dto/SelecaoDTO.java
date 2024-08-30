package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class SelecaoDTO implements Serializable {

    private String id;

    private String nome;

}
