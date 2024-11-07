package com.cbio.core.v1.dto;

import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.app.entities.ResourceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class ResourceDTO implements Serializable {
    private String id;

    private String email;

    private String dairyName;

    private Long timeAttendance;

    private Boolean active;

    private ResourceEntity.PeriodoDTO morning;
    private ResourceEntity.PeriodoDTO afternoon;
    private ResourceEntity.PeriodoDTO night;
    private ResourceEntity.PeriodoDTO dawn;

    private CompanyDTO company;

    private List<Integer> selectedDays;

    private ResourceEntity.ColorDTO color;

    private ResourceEntity.StatusEnum status;

    private String title;
    private String location;
    private String description;

    @Singular
    private List<ResourceEntity.NotificationDTO> notifications;
}
