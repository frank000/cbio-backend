package com.cbio.app.entities;

import com.cbio.core.v1.enuns.EstadosEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tier")
@Getter
@Setter
@Builder
public class TierEntity {
    
    @Id
    private String id;

    private String name;

    private Integer numAttendants;


}
