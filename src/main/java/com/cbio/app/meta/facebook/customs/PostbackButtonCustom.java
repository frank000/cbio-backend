package com.cbio.app.meta.facebook.customs;

import com.restfb.types.send.PostbackButton;
import lombok.Builder;

@Builder
public class PostbackButtonCustom extends PostbackButton {

    private String text;
    private String postbackPayload;

    public PostbackButtonCustom(String title, String postbackPayload) {
        super(title, postbackPayload);
    }
}
