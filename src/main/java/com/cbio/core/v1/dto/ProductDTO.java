package com.cbio.core.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String id;

    private DefaultPrice defaultPrice;

    private String name;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DefaultPrice {
        private String currency;
        private BigDecimal unitAmountDecimal;
    }
}
