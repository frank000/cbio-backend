package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.ComandoEntity;
import com.policia.df.bot.app.repository.ComandoRepository;
import com.policia.df.bot.app.service.mapper.ComandoMapper;
import com.policia.df.bot.app.service.mapper.CycleAvoidingMappingContext;
import com.policia.df.bot.core.service.ComandoService;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import org.springframework.stereotype.Service;

@Service
public record ComandoServiceImpl(ComandoRepository repository, ComandoMapper mapper) implements ComandoService {
    @Override
    public void adicionarComando(ComandoDTO comandoDTO) {

        ComandoEntity comandoEnviar = repository.findByNome(comandoDTO.getNome());

        if(comandoEnviar != null) {
            comandoEnviar.setDescricao(comandoDTO.getDescricao());
        } else {
            comandoEnviar = mapper.comandoDTOToComandoEntity(comandoDTO, new CycleAvoidingMappingContext());
        }

        repository.save(comandoEnviar);

    }

    @Override
    public ComandoDTO buscarPorNome(String nome) {
        return mapper.comandoEntityToComandoDTO(repository.findByNome(nome), new CycleAvoidingMappingContext());
    }
}
