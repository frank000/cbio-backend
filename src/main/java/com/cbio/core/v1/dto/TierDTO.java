package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO de Tier")
public class TierDTO implements Serializable {

    @Schema(description = "ID do Tier", example = "asd16a5sd1fa5sd1f6516c")
    private String id;

    @Schema(description = "Nome do Tier", example = "Basic")
    private String name;

    @Schema(description = "Numero de Atendentes do Tier", example = "7")
    private Integer numAttendants;

//    @Schema(description = "DTO de criação de Tier", allOf = {TierDTO.class})
//    public static class FormDTO extends TierDTO  {
//        @Parameter(hidden = true)
//        private String id;
//        // O campo id será herdado de TierDTO e visível no Swagger
//    }
}
