package com.cbio.core.v1.dto.google;

import com.cbio.core.v1.dto.CompanyDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class EventDTO implements Serializable {
    private String id;
    private String title;
    private String start;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String end;
    private String className;
    private String emailTo;
    private String description;
    private String dairyName;
    private String name;
    private String phone;
    private String email;
    private Boolean notified;
    private CompanyDTO company;

}
