package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventInvoiceDTO implements Serializable {

        private Data data;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Data {
            @JsonProperty("object")
            private InvoiceObject invoiceObject;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class InvoiceObject {
            @JsonProperty("hosted_invoice_url")
            private String hostedInvoiceUrl;

            @JsonProperty("invoice_pdf")
            private String invoicePdf;
        }
    }