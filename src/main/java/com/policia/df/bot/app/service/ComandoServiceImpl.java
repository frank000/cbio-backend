package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.ComandoEntity;
import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.app.repository.ComandoRepository;
import com.policia.df.bot.app.service.mapper.ComandoMapper;
import com.policia.df.bot.app.service.mapper.CycleAvoidingMappingContext;
import com.policia.df.bot.core.service.ComandoService;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import com.policia.df.bot.core.v1.dto.EtapaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record ComandoServiceImpl(ComandoRepository repository, ComandoMapper mapper) implements ComandoService {
    @Override
    public void adicionarComando(ComandoDTO comandoDTO) {

        ComandoEntity comandoEnviar = repository.findByNome(comandoDTO.getNome());

        if(comandoEnviar != null) {
            comandoEnviar.setDescricao(comandoDTO.getDescricao());
            comandoEnviar.setAtivo(comandoDTO.getAtivo());
        } else {
            comandoEnviar = mapper.comandoDTOToComandoEntity(comandoDTO, new CycleAvoidingMappingContext());
        }

        repository.save(comandoEnviar);

    }

    @Override
    public ComandoDTO buscarPorNome(String nome) {
        return mapper.comandoEntityToComandoDTO(repository.findByNome(nome), new CycleAvoidingMappingContext());
    }

    @Override
    public List<ComandoDTO> listarComandos() {
        return mapper.listComandoEntityToListComandoDTO(repository.findAllByAtivo(Boolean.TRUE), new CycleAvoidingMappingContext());
    }

    @Override
    public void alterar(ComandoDTO comando) throws Exception {

        if(comando.getId() == null || comando.getId().isEmpty()) throw new Exception("Informe um id para alteração.");

        ComandoEntity atual = repository.findById(comando.getId()).get();

        comando.setAtivo(comando.getAtivo() == null ? atual.getAtivo() : comando.getAtivo());

        comando.setDescricao(comando.getDescricao() == null ? atual.getDescricao() : comando.getDescricao());

        comando.setNome(comando.getNome() == null ? atual.getNome() : comando.getNome());

        comando.setNomeServico(comando.getNomeServico() == null ? atual.getNomeServico() : comando.getNomeServico());

        repository.save(mapper.comandoDTOToComandoEntity(comando, new CycleAvoidingMappingContext()));
    }
}
