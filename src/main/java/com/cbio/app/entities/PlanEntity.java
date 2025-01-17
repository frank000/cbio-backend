package com.cbio.app.entities;

import com.cbio.core.v1.dto.ProductDTO;
import com.stripe.model.Product;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("plan")
public class PlanEntity  {

    @Id
    private String id;

    private String type;

    private ProductDTO product;


}
