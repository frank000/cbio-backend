package com.cbio.app.service;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.core.service.UsuarioService;
import lombok.Data;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Component
@Data
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    @Override
    public UsuarioEntity cadastrarUsuario(UsuarioEntity usuario) {
        return repository.save(usuario);
    }

    @Override
    public UsuarioEntity buscarUsuarioPorIdUsuario(Long idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }

    @Override
    public void salvarUsuario(Update update) throws Exception {

        UsuarioEntity usuario = buscarUsuarioPorIdUsuario(update.getMessage().getChatId());

        if(usuario == null) {
            usuario = new UsuarioEntity();

            usuario.setIdUsuario(update.getMessage().getChatId());
            usuario.setFirtName(update.getMessage().getChat().getFirstName());
            usuario.setLastName(update.getMessage().getChat().getLastName());
        }

        usuario.setUltimaModificacao(System.currentTimeMillis());

        try {
            cadastrarUsuario(usuario);
        } catch (Exception e) {
            throw new Exception();
        }

    }
}
