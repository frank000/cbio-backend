package com.cbio.core.v1.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class InstagramEnuns {

    @Getter
    @AllArgsConstructor
    public static enum ButtonTypeEnum{
        WEB_URL("web_url"),
        POSTBACK("postback");
        private String value;
    }
    @Getter
    @AllArgsConstructor
    public static enum TemplateTypeEnum{
        BUTTON("button");
        private String value;
    }
    @Getter
    @AllArgsConstructor
    public static enum AttachmentTypeEnum{
        TEMPLATE("template"),
        AUDIO("audio"),
        VIDEO("video"),
        IMAGE("image");
        private String value;
    }
}
