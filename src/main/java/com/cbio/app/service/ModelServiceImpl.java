package com.cbio.app.service;

import com.cbio.app.entities.ModelEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.ModelRepository;
import com.cbio.app.service.mapper.ModelMapper;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.ModelService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.SelecaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Override
    public ModelDTO save(ModelDTO dto) throws CbioException {

        isValidTemplate(dto);

        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            dto.setCompany(CompanyDTO.builder().id(companyIdUserLogged).build());
            ModelEntity entity = modelMapper.toEntity(dto);
            return modelMapper.toDto(modelRepository.save(entity));
        }else{
            throw new CbioException("Companhia não encontrada.", HttpStatus.BAD_REQUEST.value());
        }

    }

    @Override
    public ModelDTO update(ModelDTO dto) throws CbioException {
        isValidTemplate(dto);

        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (!ObjectUtils.isEmpty(companyIdUserLogged)) {
            ModelEntity modelEntity = getModelEntity(dto.getId());

            modelMapper.fromDto(dto, modelEntity);

//            dto.setCompany(CompanyDTO.builder().id(companyIdUserLogged).build());
//            ModelEntity entity = modelMapper.toEntity(dto);
            return modelMapper.toDto(modelRepository.save(modelEntity));
        }else{
            throw new CbioException("Companhia não encontrada.", HttpStatus.BAD_REQUEST.value());
        }

    }

    private static void isValidTemplate(ModelDTO dto) throws CbioException {
        if(!StringUtils.hasText(dto.getName())){
            throw new CbioException("Nome do modelo obrigatório.", HttpStatus.BAD_REQUEST.value());
        }

        if(dto.getBody() != null && !StringUtils.hasText(dto.getBody().getLabel())){
            throw new CbioException("Modelo é obrigatório.", HttpStatus.BAD_REQUEST.value());
        }

        if(dto.getBody() != null && !CollectionUtils.isEmpty(dto.getBody().getParameters())){
            boolean hasParameterEmpty = dto.getBody().getParameters().stream()
                    .anyMatch(parameter -> !StringUtils.hasText(parameter.getValue()));
            if(hasParameterEmpty){
                throw new CbioException("Parâmentro é obrigatório.", HttpStatus.BAD_REQUEST.value());
            }
        }
    }


    @Override
    public ModelDTO getById(String id) throws CbioException {
        ModelEntity entity = getModelEntity(id);
        return modelMapper.toDto(entity);
    }

    private ModelEntity getModelEntity(String id) throws CbioException {
        ModelEntity entity = modelRepository.findById(id)
                .orElseThrow(() -> new CbioException("Modelo não encontrado.", HttpStatus.NO_CONTENT.value()));
        return entity;
    }

    @Override
    public List<ModelDTO> listAll() {
        return modelMapper.toDto(modelRepository.findAll());
    }

    @Override
    public List<SelecaoDTO> listSelection() {
        return modelRepository.findAll()
                .stream()
                .map(modelEntity ->
                    SelecaoDTO.builder()
                            .id(modelEntity.getId())
                            .nome(modelEntity.getName())
                            .build()
                )
                .collect(Collectors.toList());


    }

    @Override
    public void delete(String id) throws CbioException {
        ModelEntity entity = getModelEntity(id);
        modelRepository.delete(entity);
    }

    public ModelDTO getByName(String name) throws CbioException {
        ModelEntity modelEntity = modelRepository.findByName(name)
                .orElseThrow(() -> new CbioException("Modelo não encontrado.", HttpStatus.NOT_FOUND.value()));
        return modelMapper.toDto(modelEntity);

    }


}
