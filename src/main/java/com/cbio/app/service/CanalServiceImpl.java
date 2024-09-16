package com.cbio.app.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.repository.CanalRepository;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.core.service.CanalService;
import com.cbio.core.v1.dto.CanalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CanalServiceImpl implements CanalService {

    private final CanalRepository canalRepository;
    private final CanalMapper mapper;


    @Override
    public List<CanalEntity> listarTodos() {
        return canalRepository.findAll();
    }

    @Override
    public CanalDTO incluirCanal(CanalDTO canal) {

        CanalEntity save = canalRepository.save(mapper.canalDTOToCanalEntity(canal, new CycleAvoidingMappingContext()));
        return mapper.canalEntityToCanalDTO(save, new CycleAvoidingMappingContext());
    }

    @Override
    public Optional<CanalEntity> findCanalByTokenAndCliente(String token, String cliente) throws Exception {
        try {
            return canalRepository.findCanalByTokenAndClienteAndAtivoTrue(token, cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar canal.");
        }
    }

    @Override
    public Boolean existsByTokenAndCliente(String token, String nomeCanal) throws Exception {
        try {
            return canalRepository.existsByTokenAndNomeAndAtivoTrue(token, nomeCanal);
        } catch (Exception e) {
            throw new Exception("Erro ao consultar canal.");
        }
    }

    @Override
    public void alterar(CanalDTO canal) throws Exception {

        if(canal.getId() == null || canal.getId().isEmpty()) throw new Exception("Informe um id para alteração.");

        CanalEntity atual = canalRepository.findById(canal.getId()).get();

        canal.setIdCanal(canal.getIdCanal() == null ? atual.getIdCanal() : canal.getIdCanal());

        canal.setNome(canal.getNome() == null ? atual.getNome() : canal.getNome());

        canal.setCliente(canal.getCliente() == null ? atual.getCliente() : canal.getCliente());

        canal.setToken(canal.getToken() == null ? atual.getToken() : canal.getToken());

        canal.setApiKey(canal.getApiKey() == null ? atual.getApiKey() : canal.getApiKey());

        canal.setPrimeiroNome(canal.getPrimeiroNome() == null ? atual.getPrimeiroNome() : canal.getPrimeiroNome());

        canal.setUserName(canal.getUserName() == null ? atual.getUserName() : canal.getUserName());

        canalRepository.save(mapper.canalDTOToCanalEntity(canal, new CycleAvoidingMappingContext()));
    }

    public void deleta(String id){
        canalRepository.deleteById(id);
    }

    @Override
    public CanalDTO obtemPorId(String id) {
        CanalEntity entity = canalRepository.findById(id).orElseThrow();
        return mapper.canalEntityToCanalDTO(entity, new CycleAvoidingMappingContext());
    }

    @Override
    public void delete(String id) {
        canalRepository.deleteById(id);
    }
}
