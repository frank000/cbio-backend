package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.app.repository.CanalRepository;
import com.policia.df.bot.core.service.CanalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record CanalServiceImpl(CanalRepository repository) implements CanalService {

    @Override
    public List<CanalEntity> listarTodos() {
        return repository.findAll();
    }
}
