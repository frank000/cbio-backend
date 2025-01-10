package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ItemSelecaoDTO implements Serializable {

    private String id;

    private String label;

}
