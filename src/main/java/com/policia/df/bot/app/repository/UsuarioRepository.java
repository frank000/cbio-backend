package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.app.entities.UsuarioEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<UsuarioEntity, String> {

    public UsuarioEntity findByIdUsuario(Long idUsuario);

}
