package com.cbio.app.entities;

import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.core.v1.dto.CompanyDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Document("resource")
@Getter
@Setter
@Builder
public class ResourceEntity {
    
    @Id
    private String id;

    private String email;

    @Indexed
    private String dairyName;

    private Long timeAttendance;

    private Boolean active;

    private PeriodoDTO morning;
    private PeriodoDTO afternoon;
    private PeriodoDTO night;
    private PeriodoDTO dawn;

    private CompanyDTO company;

    private ColorDTO color;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private List<Integer> selectedDays;

    private String title;
    private String location;
    private String description;

    @Singular
    private List<NotificationDTO> notifications;

    @Getter
    @Setter
    @Builder
    public static class PeriodoDTO{
        private String init;
        private String end;

        public Boolean isLocalDateTimeBetween(LocalDateTime localDateTime){

            // Converte as strings init e end para LocalTime
            LocalTime startTime = LocalTime.parse(init);
            LocalTime endTime = LocalTime.parse(end);
            LocalTime currentTime = localDateTime.toLocalTime();

            // Verifica se o período cruza a meia-noite
            if (startTime.isBefore(endTime)) {
                // Período normal (não cruza a meia-noite)
                return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime); // Exclui endTime
            } else {
                // Período que cruza a meia-noite
                return !currentTime.isBefore(startTime) || currentTime.isBefore(endTime); // Exclui endTime
            }
        }

        @NotNull
        private String getDateTimeString(LocalDateTime localDateTime, String hour) {
            return localDateTime.toLocalDate().toString().concat("T").concat(hour);
        }
    }

    @Getter
    @Setter
    @Builder
    public static class ColorDTO implements Serializable {
        private String label;
        private String classColor;
        private String color;
    }

    @Getter
    @Setter
    @Builder
    public static class NotificationDTO{

        @Enumerated(EnumType.STRING)
        private CanalSenderEnum channel;
        private Long antecedence;
        private String model;
    }

    public enum StatusEnum{
        DESYNC,
        SYNC;
    }
}
