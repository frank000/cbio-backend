package com.cbio.app.service;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.core.service.UsuarioTelegramService;
import lombok.Data;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Component
@Data
public class UsuarioTelegramServiceImpl implements UsuarioTelegramService {

    private final UsuarioRepository repository;
    @Override
    public UsuarioEntity cadastrarUsuario(UsuarioEntity usuario) {
        return repository.save(usuario);
    }

    @Override
    public UsuarioEntity buscarUsuarioPorIdUsuario(Long idUsuario) {
        return repository.findByIdentificadorUsuario(idUsuario);
    }

    @Override
    public void salvarUsuario(Update update) throws Exception {

        UsuarioEntity usuario = buscarUsuarioPorIdUsuario(update.getMessage().getChatId());

        if(usuario == null) {
            usuario = UsuarioEntity.builder()
                    .identificadorUsuario(update.getMessage().getChatId())
                    .name(update.getMessage().getChat().getFirstName()
                            .concat(" ")
                            .concat(update.getMessage().getChat().getLastName()))
                    .build();
        }

        usuario.setUltimaModificacao(System.currentTimeMillis());

        try {
            cadastrarUsuario(usuario);
        } catch (Exception e) {
            throw new Exception();
        }

    }
}
