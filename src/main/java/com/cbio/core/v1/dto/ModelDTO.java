package com.cbio.core.v1.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ModelDTO implements Serializable {

    private String id;
    private String name;
    private Body header;
    private Body body;
    private Body footer;
    @Singular
    private List<Button> buttons;
    private ParseMode parseMode;
    private CompanyDTO company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Body implements Serializable {

        private static final long serialVersionUID = 7819044076339865113L;

        private String label;

        @Singular
        @Getter(AccessLevel.NONE)
        private List<Parameter> parameters;

        public List<Parameter> getParameters() {
            if(parameters == null) {
                parameters = new ArrayList<>();
            }
            return parameters;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Button implements Serializable {

        private static final long serialVersionUID = 5346889283774756264L;

        public enum Type {
            URL("url");

            @Getter
            private String valor;

            Type(String valor) {
                this.valor = valor;
            }
        }

        private String label;
        private String url;

        private Type type;
        @Singular
        private List<Parameter> parameters;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Parameter implements Serializable {
        public enum ParameterType {

            TEXT("text"),
            IMAGE("image"),
            VIDEO("video");

            @Getter
            private String valor;

            ParameterType(String valor) {
                this.valor = valor;
            }
        }

        private ParameterType type;
        private String value;
    }

    public enum ParseMode {
        HTML,
        MARKDOWN
    }

}
