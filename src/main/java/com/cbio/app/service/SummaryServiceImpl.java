package com.cbio.app.service;

import com.cbio.app.repository.SummaryCustomRepository;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.SummaryService;
import com.cbio.core.v1.dto.SummaryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SummaryServiceImpl implements SummaryService {

    private final SummaryCustomRepository summaryCustomRepository;
    private final AuthService authService;

    public Map<String, List<SummaryDTO>> perAttedantByPeriod(String dateInitString, String dateEndString ) {
        String companyId = authService.getCompanyIdUserLogged();
        if(ObjectUtils.isNotEmpty(companyId)){
            List<SummaryDTO> sumaryByCompanyAndPeriod = summaryCustomRepository.getSumaryByCompanyAndPeriod(companyId, dateInitString, dateEndString);
            Map<String, List<SummaryDTO>> collect = sumaryByCompanyAndPeriod.stream().collect(Collectors.groupingBy(SummaryDTO::getName));
            return collect;

        }else{
            return null;
        }

    }

    @Override
    public Map<String, List<SummaryDTO>> perMonthOfYear() {
        String companyId = authService.getCompanyIdUserLogged();
        if(ObjectUtils.isNotEmpty(companyId)){
            List<SummaryDTO> summaryDTOS = summaryCustomRepository.performAggregationperMonth(companyId);
            List<String> meses = Arrays.asList("Janeiro", "Fevereiro", "Março", "Abril", "Maio",
                    "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro");

            Map<String, List<SummaryDTO>> result = summaryDTOS.stream().collect(Collectors.groupingBy(SummaryDTO::getMes));

            Map<String, List<SummaryDTO>> unsortedMap = new HashMap<>();
            meses
                    .forEach(mes ->{
                        unsortedMap.put(mes, ObjectUtils.defaultIfNull( result.get(mes), List.of()));
                    });
            List<String> monthOrder = Arrays.asList(
                    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
            );

            Map<String, List<SummaryDTO>> sortedMap = new LinkedHashMap<>();
            for (String month : monthOrder) {
                if (unsortedMap.containsKey(month)) {
                    sortedMap.put(month, unsortedMap.get(month));
                }
            }
            return sortedMap;
        }else{
            return null;
        }


    }
}
