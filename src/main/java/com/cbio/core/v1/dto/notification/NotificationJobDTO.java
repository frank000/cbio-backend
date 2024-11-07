package com.cbio.core.v1.dto.notification;

import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.google.EventDTO;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationJobDTO implements Serializable {


    private String email;
    private String dairyName;
    private CompanyDTO company;
    private String title;
    private String location;
    private String description;
    private List<ResourceEntity.NotificationDTO> notifications;
    private List<EventDTO> events;

    public Optional<ResourceEntity.NotificationDTO> getNotificationByCanal(CanalSenderEnum sender) {
         return notifications
                .stream()
                .filter(notificationDTO -> notificationDTO.getChannel().equals(sender))
                .findFirst();
    }


}
