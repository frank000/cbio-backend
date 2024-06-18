package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.app.repository.CanalRepository;
import com.policia.df.bot.core.service.CanalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record CanalServiceImpl(CanalRepository repository) implements CanalService {

    @Override
    public List<CanalEntity> listarTodos() {
        return repository.findAll();
    }

    @Override
    public CanalEntity incluirCanal(CanalEntity canal) {
        return repository.save(canal);
    }

    @Override
    public CanalEntity findCanalByTokenAndCliente(String token, String cliente) throws Exception {
        try {
            return repository.findCanalByTokenAndCliente(token, cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar canal.");
        }
    }


}
