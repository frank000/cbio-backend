package com.cbio.app.service;

import com.cbio.app.entities.ComandoEntity;
import com.cbio.app.repository.ComandoRepository;
import com.cbio.app.service.mapper.ComandoMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.core.service.ComandoService;
import com.cbio.core.v1.dto.ComandoDTO;
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
        return mapper.listComandoEntityToListComandoDTO(repository.findAll(), new CycleAvoidingMappingContext());
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
