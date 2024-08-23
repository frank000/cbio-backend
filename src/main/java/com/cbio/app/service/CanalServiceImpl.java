package com.cbio.app.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.repository.CanalRepository;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.core.service.CanalService;
import com.cbio.core.v1.dto.CanalDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record CanalServiceImpl(CanalRepository repository, CanalMapper mapper) implements CanalService {

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
            return repository.findCanalByTokenAndClienteAndAtivoTrue(token, cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar canal.");
        }
    }

    @Override
    public Boolean existsByTokenAndCliente(String token, String nomeCanal) throws Exception {
        try {
            return repository.existsByTokenAndNomeAndAtivoTrue(token, nomeCanal);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar canal.");
        }
    }

    @Override
    public void alterar(CanalDTO canal) throws Exception {

        if(canal.getId() == null || canal.getId().isEmpty()) throw new Exception("Informe um id para alteração.");

        CanalEntity atual = repository.findById(canal.getId()).get();

        canal.setIdCanal(canal.getIdCanal() == null ? atual.getIdCanal() : canal.getIdCanal());

        canal.setNome(canal.getNome() == null ? atual.getNome() : canal.getNome());

        canal.setCliente(canal.getCliente() == null ? atual.getCliente() : canal.getCliente());

        canal.setToken(canal.getToken() == null ? atual.getToken() : canal.getToken());

        canal.setApiKey(canal.getApiKey() == null ? atual.getApiKey() : canal.getApiKey());

        canal.setPrimeiroNome(canal.getPrimeiroNome() == null ? atual.getPrimeiroNome() : canal.getPrimeiroNome());

        canal.setUserName(canal.getUserName() == null ? atual.getUserName() : canal.getUserName());

        repository.save(mapper.canalDTOToCanalEntity(canal, new CycleAvoidingMappingContext()));
    }

    public void deleta(String id){
        repository.deleteById(id);
    }

}
