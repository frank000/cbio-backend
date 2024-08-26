package com.cbio.chat.models;

import com.cbio.core.v1.dto.CanalDTO;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@Document(collection = "dialogo")
public class DialogoEntity {

  @Id
  private String id;

  private String mensagem;

  private String identificadorRemetente;

  private CanalDTO canal;

  private String channelUuid;

}