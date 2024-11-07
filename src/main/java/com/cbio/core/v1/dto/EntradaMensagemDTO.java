package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class EntradaMensagemDTO {

    private String uuid;
    private String mensagem;
    private MediaDTO media;
    private String type;
    private CanalDTO canal;
    private String identificadorRemetente;
    private Object mensagemObject;
    private MultipartFile file;

}
