package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ModelDTO;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("model")
@Getter
@Setter
@Builder
public class ModelEntity {

    @Id
    private String id;


    private String name;
    private ModelDTO.Body header;
    private ModelDTO.Body body;
    private ModelDTO.Body footer;
    @Singular
    private List<ModelDTO.Button> buttons;
    private ModelDTO.ParseMode parseMode;
    private CompanyDTO company;

}
