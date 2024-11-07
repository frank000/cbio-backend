package com.cbio.core.v1.dto.google;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Setter
@Getter
public class QueryFilterCalendarDTO implements Serializable {

    private String resourceId;
    private LocalDateTime start;
    private LocalDateTime end;

    private String startStr;

    private String endStr;
    private Map<String, Object> view;
}
