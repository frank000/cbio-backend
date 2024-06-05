package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.UsuarioEntity;
import com.policia.df.bot.app.repository.UsuarioRepository;
import com.policia.df.bot.core.service.UsuarioService;
import lombok.Data;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;

@Service
@Component
@Data
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    @Override
    public UsuarioEntity cadastrarUsuario(UsuarioEntity usuario) {

//        UsuarioEntity usuExistente = repository.findByIdUsuario(usuario.getIdUsuario());
//
//        if(usuExistente != null) {
//
//        }

        return repository.save(usuario);
    }

    @Override
    public UsuarioEntity buscarUsuarioPorIdUsuario(Long idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }
}
