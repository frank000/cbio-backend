package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("phrase")
public class PhraseEntity {

    @Id
    private String id;

    private String description;

    private Boolean ativo;

    private CompanyDTO company;

}
