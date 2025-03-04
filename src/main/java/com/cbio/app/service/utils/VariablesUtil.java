package com.cbio.app.service.utils;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.google.EventDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class VariablesUtil {

    public static class Event{
        public static final String NO_INFORMED = "não informado";
        public static Map<String, Object> populateVariablesToParametersEvent(
                String email, CompanyDTO company, EventDTO eventDTO, SessaoEntity sessaoEntity) {
            Map<String, Object> map = new HashMap<>();
            map.put("nome", ObjectUtils.isNotEmpty(sessaoEntity.getNome()) ? sessaoEntity.getNome() : NO_INFORMED);
            map.put("email", ObjectUtils.isNotEmpty(sessaoEntity.getEmail()) ? sessaoEntity.getEmail() : email);
            map.put("cpf", ObjectUtils.isNotEmpty(sessaoEntity.getCpf()) ? sessaoEntity.getCpf() : NO_INFORMED);
            map.put("dataHoraAtendimentoAberto", getDateTimeFormatedBySession(sessaoEntity.getDataHoraAtendimentoAberto()));
            map.put("atendenteNome", (ObjectUtils.isNotEmpty(sessaoEntity.getUltimoAtendente())) ? sessaoEntity.getUltimoAtendente().getName() : "");
            map.put("title", getVariable(eventDTO.getTitle()));
            map.put("description", getVariable(eventDTO.getDescription()));
            map.put("dairyName", getVariable(eventDTO.getDairyName()));
            map.put("start", CbioDateUtils.getDateTimeWithSecFormated(CbioDateUtils.LocalDateTimes.getFrom(eventDTO.getStart().replace(CbioDateUtils.MINUS_3,"")), CbioDateUtils.FORMAT_BRL_DATE_TIME));
            map.put("end", CbioDateUtils.getDateTimeWithSecFormated(CbioDateUtils.LocalDateTimes.getFrom(eventDTO.getEnd().replace(CbioDateUtils.MINUS_3,"")), CbioDateUtils.FORMAT_BRL_DATE_TIME));
            map.put("company.name", ObjectUtils.isNotEmpty(company) ? getVariable(company.getNome()) : NO_INFORMED);
            return map;
        }
    }

    public static class BusinessCard{
        public static final String NO_INFORMED = "não informado";
        public static Map<String, Object> populateVariablesToParameters(
                String email, CompanyDTO company, SessaoEntity sessaoEntity) {
            Map<String, Object> map = new HashMap<>();
            map.put("nome", ObjectUtils.isNotEmpty(sessaoEntity.getNome()) ? sessaoEntity.getNome() : NO_INFORMED);
            map.put("email", ObjectUtils.isNotEmpty(sessaoEntity.getEmail()) ? sessaoEntity.getEmail() : email);
            map.put("cpf", ObjectUtils.isNotEmpty(sessaoEntity.getCpf()) ? sessaoEntity.getCpf() : NO_INFORMED);
            map.put("dataHoraAtendimentoAberto", getDateTimeFormatedBySession(sessaoEntity.getDataHoraAtendimentoAberto()));
            map.put("atendenteNome", (ObjectUtils.isNotEmpty(sessaoEntity.getUltimoAtendente())) ? sessaoEntity.getUltimoAtendente().getName() : "");
            map.put("company.name", ObjectUtils.isNotEmpty(company) ? getVariable(company.getNome()) : NO_INFORMED);
            return map;
        }
    }



    private static String getVariable(String variable) {
        return (ObjectUtils.isNotEmpty(variable)) ? variable : Event.NO_INFORMED;
    }


    @NotNull
    private static String getDateTimeFormatedBySession(LocalDateTime dataHoraAtendimentoAberto) {

        if (ObjectUtils.isNotEmpty(dataHoraAtendimentoAberto)) {
            return CbioDateUtils.getDateTimeFormated(dataHoraAtendimentoAberto, CbioDateUtils.FORMAT_BRL_DATE_TIME, CbioDateUtils.PLUS_3);
        } else {
            return Event.NO_INFORMED;
        }
    }

    public String handleAndGetPhoneNumber(String phoneStr) {

        String ddd = phoneStr.substring(0, 2);
        String phoneWithoutDDD = phoneStr.substring(2);
        String phone;
        if (phoneWithoutDDD.length() == 9) {
            phone = ddd + phoneStr.substring(2).substring(1);
        } else {
            phone = phoneStr;
        }
        String phoneBrazilianPrefix = "55" + phone;
        return phoneBrazilianPrefix;
    }

}
