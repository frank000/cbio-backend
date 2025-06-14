package com.cbio.app.web.login.v1;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.StatusPaymentEnum;
import com.cbio.app.exception.CbioException;
import com.cbio.app.service.utils.JwtUtil;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.LoginService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.LoginDTO;
import com.cbio.core.v1.dto.LoginResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/v1/login")
@RestController
public class LoginController {

    private final LoginService loginService;
    private final AuthService authService;
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO) throws JsonProcessingException, CbioException {

        LoginResultDTO login = loginService.login(loginDTO.getEmail(), loginDTO.getPassword());

        Optional<String> companyId = JwtUtil.getClaimWithoutVerification(login.getAccess_token(), "companyId");

        if(companyId.isPresent()){
            CompanyDTO companyDTO = companyService.findById(companyId.get());
            StatusPaymentEnum statusPayment = companyDTO.getStatusPayment();

            if(StatusPaymentEnum.TRIAL.equals(statusPayment) && CbioDateUtils.LocalDateTimes.now().isAfter(companyDTO.getDataAlteracaoStatus().plusDays(30)) ||
                    StatusPaymentEnum.DOIS_DIAS.equals(statusPayment) && CbioDateUtils.LocalDateTimes.now().isAfter(companyDTO.getDataAlteracaoStatus().plusDays(2)) ) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "Sua versão trial chegou ao fim. Entre em contato com nossos consultores.");

                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(map);
            }else if(StatusPaymentEnum.CANCELLED.equals(statusPayment)) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "Sua pagamento foi cancelado pelo usuário. Entre em contato com nossos consultores.");

                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(map);
            }
        }


        return ResponseEntity.ok().body(login);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String refreshToken) {

        loginService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }
}
