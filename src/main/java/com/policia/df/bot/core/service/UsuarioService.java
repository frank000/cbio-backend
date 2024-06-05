package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.UsuarioEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UsuarioService {

    public UsuarioEntity cadastrarUsuario(UsuarioEntity usuario);

    public UsuarioEntity buscarUsuarioPorIdUsuario(Long idUsuario);

    void salvarUsuario(Update update) throws Exception;

}
