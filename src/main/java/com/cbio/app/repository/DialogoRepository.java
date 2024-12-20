package com.cbio.app.repository;

import com.cbio.chat.models.DialogoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DialogoRepository extends MongoRepository<DialogoEntity, String> {

    List<DialogoEntity> findAllByIdentificadorRemetenteOrderByCreatedDateTime(String identificadorRementente);

    List<DialogoEntity> findAllBySessionIdOrderByCreatedDateTime(String sessionId);

    Optional<DialogoEntity> getByMediaIsNullAndId(Long id);

    Long countDialogoEntitiesByUuid(String uuid);
}
