package com.cbio.app.service;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public Map<String, Object> getClaimsUserLogged() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt) {

            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaims();

        }else{
            return Map.of();
        }
    }


    public String getCompanyIdUserLogged(){
        Map<String, Object> claimsUserLogged = getClaimsUserLogged();

        return ObjectUtils.defaultIfNull((String)claimsUserLogged.get("companyId"), null) ;

    }
}
