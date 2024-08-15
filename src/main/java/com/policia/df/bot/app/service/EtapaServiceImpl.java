package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.app.repository.EtapaRepository;
import com.policia.df.bot.app.service.mapper.CycleAvoidingMappingContext;
import com.policia.df.bot.app.service.mapper.EtapaMapper;
import com.policia.df.bot.core.service.EtapaService;
import com.policia.df.bot.core.v1.dto.EtapaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record EtapaServiceImpl(EtapaRepository repository, EtapaMapper mapper) implements EtapaService {

    @Override
    public void adicionarEtapa(EtapaDTO etapa) {

        repository.save(mapper.etapaDTOToEtapaEntity(etapa, new CycleAvoidingMappingContext()));
    }

    @Override
    public List<EtapaDTO> listar() {
        return mapper.listEtapaEntityToListEtapaDTO(repository.findAll(), new CycleAvoidingMappingContext());
    }

    @Override
    public void alterar(EtapaDTO etapaDTO) throws Exception {

        if(etapaDTO.getId() == null || etapaDTO.getId().isEmpty()) throw new Exception("Informe um id para alteração.");

        EtapaEntity atual = repository.findById(etapaDTO.getId()).get();

        etapaDTO.setComandoEtapa(etapaDTO.getComandoEtapa() == null ? atual.getComandoEtapa() : etapaDTO.getComandoEtapa());

        etapaDTO.setDescricaoEtapa(etapaDTO.getDescricaoEtapa() == null ? atual.getDescricaoEtapa() : etapaDTO.getDescricaoEtapa());

        etapaDTO.setNomeEtapa(etapaDTO.getNomeEtapa() == null ? atual.getNomeEtapa() : etapaDTO.getNomeEtapa());

        etapaDTO.setMensagemEtapa(etapaDTO.getMensagemEtapa() == null ? atual.getMensagemEtapa() : etapaDTO.getMensagemEtapa());

        etapaDTO.setProximaEtapa(etapaDTO.getProximaEtapa() == null ? atual.getProximaEtapa() : etapaDTO.getProximaEtapa());

        etapaDTO.setTipoEtapa(etapaDTO.getTipoEtapa() == null ? atual.getTipoEtapa() : etapaDTO.getTipoEtapa());

        etapaDTO.setAtivo(etapaDTO.getAtivo() == null ? atual.getAtivo() : etapaDTO.getAtivo());

        repository.save(mapper.etapaDTOToEtapaEntity(etapaDTO, new CycleAvoidingMappingContext()));
    }

    Boolean isEmpty(Object obj) {
        return (obj == null || obj == "");
    }
}
