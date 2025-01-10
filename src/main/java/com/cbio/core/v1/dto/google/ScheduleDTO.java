package com.cbio.core.v1.dto.google;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ScheduleDTO implements Serializable {
    private String id;

    private LocalDateTime start;
    private String strStartDateTime;
    private String strEndDateTime;
    private String strDate;
    private String strHour;


}
