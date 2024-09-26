package com.cbio.chat.models;

import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.MediaDTO;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@Document("dialogo")
public class DialogoEntity {

  @Id
  private String id;

  private String mensagem;

  private MediaDTO media;

  @Indexed
  private String identificadorRemetente;

  private String type;

  private String toIdentifier;

  private CanalDTO canal;

  private String channelUuid;

  private String from;

  private LocalDateTime createdDateTime;

  @Indexed(unique = true)
  private String uuid;


}