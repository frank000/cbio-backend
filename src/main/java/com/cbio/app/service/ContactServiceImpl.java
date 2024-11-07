package com.cbio.app.service;

import com.cbio.app.entities.ContactEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.ContactRepository;
import com.cbio.app.service.mapper.ContactMapper;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.ContactService;
import com.cbio.core.service.EventService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ContactDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final AuthService authService;


    @Override
    public ContactDTO save(ContactDTO contactDTO) throws CbioException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        if(StringUtils.hasText(companyIdUserLogged)){
            contactDTO.setCompany(
                    CompanyDTO.builder()
                            .id(companyIdUserLogged)
                            .build()
            );
            contactDTO.setPhone(contactDTO.getPhone().replaceAll("\\D", ""));
            ContactEntity entity = contactMapper.toEntity(contactDTO);
            return contactMapper.toDto(contactRepository.save(entity));

        }else{

            throw new CbioException("Companhia não encontrada", HttpStatus.NO_CONTENT.value());
        }
    }

    @Override
    public ContactDTO update(ContactDTO contactDTO) throws CbioException {
        ContactEntity entity = getContactEntity(contactDTO.getId());

        contactMapper.fromDto(contactDTO, entity);
        return contactMapper.toDto(contactRepository.save(entity));

    }

    private ContactEntity getContactEntity(String id) throws CbioException {
        ContactEntity entity = contactRepository.findById(id)
                .orElseThrow(() -> new CbioException("Contato não encontrado", HttpStatus.NO_CONTENT.value()));
        return entity;
    }

    @Override
    public ContactDTO getContact(String id) throws CbioException {
        return contactMapper.toDto(getContactEntity(id));
    }

    @Override
    public List<ContactDTO> getContacts() throws CbioException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if(StringUtils.hasText(companyIdUserLogged)){
            return contactMapper.toDto(contactRepository.findByCompanyId(companyIdUserLogged));
        }else{
            throw new CbioException("Companhia não encontrada", HttpStatus.NO_CONTENT.value());
        }

    }

    @Override
    public void delete(String id) throws CbioException {
        ContactEntity contactEntity = getContactEntity(id);
        contactRepository.delete(contactEntity);
    }
}
