package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@Document("event")
public class EventEntity implements Serializable {

    @Id
    private String id;

    private String title;
    @Indexed
    private String start;
    private String end;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String className;
    private String emailTo;
    private String description;
    private String dairyName;
    private String contactId;
    private String name;
    private String phone;
    private String email;
    private CompanyDTO company;
    @Getter(AccessLevel.NONE)
    private Boolean notified;

    public Boolean getNotified() {
        if(notified == null) {
            notified = Boolean.FALSE;
        }
        return notified;
    }
}
