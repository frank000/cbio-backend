package com.cbio.app.repository;

import com.cbio.chat.models.DialogoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DialogoRepository extends MongoRepository<DialogoEntity, String> {

    List<DialogoEntity> findAllByIdentificadorRemetenteOrderByCreatedDateTime(String identificadorRementente);

    @Query(
            value = "{ 'sessionId' : ?0 }",
            sort = "{ 'createdDateTime' : -1 }"
    )
    Page<DialogoEntity> findAllBySessionIdOrderByCreatedDateTime(String sessionId, Pageable pageable);

    Optional<DialogoEntity> getByMediaIsNullAndId(Long id);

    Long countDialogoEntitiesByUuid(String uuid);
}
