package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.whatsapp.api.domain.messages.InteractiveMessage;
import com.whatsapp.api.domain.messages.type.MessageType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class WebhookEvent extends GetFirstAbstract<WebhookEvent.Entry> {

    private String object;
    private List<Entry> entry;


    @Override
    public List<Entry> getContents() {
        return ObjectUtils.defaultIfNull(entry, List.of());
    }

    public Entry getFirstEntry() {
        return entry.isEmpty() ? null : entry.get(0);
    }
// Getters e setters

    @Getter
    @Setter
    public static class Entry extends GetFirstAbstract<WebhookEvent.Change> {
        private String id;
        private List<Change> changes;

        @Override
        public List<Change> getContents() {
            return ObjectUtils.defaultIfNull(changes, List.of());
        }

        public Change getFirstChange() {
            return changes.isEmpty() ? null : changes.get(0);
        }
        // Getters e setters
    }

    @Getter
    @Setter
    public static class Change {
        private String field;
        private Value value;

        public static boolean hasMessageOrButtonAction(com.whatsapp.api.domain.webhook.Value value) {
            if(value.messages() == null){
                return false;
            }else{
                boolean isInteractive = MessageType.INTERACTIVE.equals(value.messages().get(0).type());


                boolean hasValueAndHasMenssage = value != null && value.messages() != null && !value.messages().isEmpty();

                if(isInteractive){
                    assert value.messages() != null;
                    return StringUtils.hasText(value.messages().get(0).interactive().buttonReply().title());
                }else{
                    boolean hasTextMessage = value.messages().stream().findFirst().isPresent()
                            && StringUtils.hasText(
                            value.messages().stream().findFirst()
                                    .get()
                                    .text().body());
                    return hasTextMessage;
                }

            }
        }
    }

    @Getter
    @Setter
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        private Metadata metadata;
        private List<Contact> contacts;
        private List<Message> messages;

        // Getters e setters
    }

    @Getter
    @Setter
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        @JsonProperty("phone_number_id")
        private String phoneNumberId;

        // Getters e setters
    }

    @Getter
    @Setter
    public static class Contact {
        private Profile profile;
        @JsonProperty("wa_id")
        private String waId;

        public static Boolean hasContact(com.whatsapp.api.domain.webhook.Value value) {
            if (value != null && value.contacts() != null && !value.contacts().isEmpty() &&

                    StringUtils.hasText(
                            value.contacts().stream().findFirst()
                                    .get()
                                    .waId()
                    )
            ) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }

    @Getter
    @Setter
    public static class Profile {
        private String name;

        // Getters e setters
    }

    @Getter
    @Setter
    public static class Message {
        private String from;
        private String id;
        private String timestamp;
        private String type;
        private Text text;

        // Getters e setters
    }

    @Getter
    @Setter
    public static class Text {
        private String body;

        // Getters e setters
    }
}
