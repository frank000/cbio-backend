package com.cbio.app.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.ws.rs.Produces;
import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

@FeignClient(name = "instagramClient", url = "https://graph.instagram.com/v21.0")
public interface InstagramFeignClient {

    @PostMapping("/{igId}/messages")
    String sendMessage(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody InstagramMessage message,
            @RequestParam("igId") String igId
    );

    @PostMapping("/me/messages")
    @Produces(value = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String sendMeMessage(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody InstagramMessage message
    );


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InstagramMessage implements Serializable {

        private Recipient recipient;
        private Message message;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Recipient implements Serializable {
            private String id;

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Message implements Serializable {
            private String text;
            private Attachment attachment;

            @Getter
            @Setter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class Attachment implements Serializable {

                private String type;

                private Payload payload;

                @Getter
                @Setter
                @NoArgsConstructor
                @AllArgsConstructor
                @Builder
                public static class Payload implements Serializable {
                    private String url;

                    @JsonProperty("template_type")
                    private String templateType;
                    private String text;
                    private List<Buttons> buttons;

                }
            }
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Buttons implements Serializable {

            @Enumerated(EnumType.STRING)
            private String type;
            private String url;
            private String payload;
            private String title;
        }

    }

}
