package com.cbio.core.v1.dto;

import com.stripe.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {
    Product[] items;
    String customerName;
    String customerEmail;
    String subscriptionId;

}
