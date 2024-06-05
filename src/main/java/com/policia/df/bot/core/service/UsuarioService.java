package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.UsuarioEntity;

public interface UsuarioService {

    public UsuarioEntity cadastrarUsuario(UsuarioEntity usuario);

    public UsuarioEntity buscarUsuarioPorIdUsuario(Long idUsuario);

}
