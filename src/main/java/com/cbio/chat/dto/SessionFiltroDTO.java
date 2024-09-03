package com.cbio.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class SessionFiltroDTO implements Serializable {

    private String attendantId;
    private String companyId;
}
