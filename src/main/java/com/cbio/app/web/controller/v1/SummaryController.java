package com.cbio.app.web.controller.v1;


import com.cbio.core.service.SummaryService;
import com.cbio.core.v1.dto.SummaryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/per-period")
    ResponseEntity<Map<String, List<SummaryDTO>>> getSummaryPerPeriod(
            @RequestParam(name = "initDate", required = false) String initDate,
            @RequestParam(name = "endDate", required = false) String endDate){


        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        initDate = ObjectUtils.defaultIfNull(initDate, LocalDate.now().minusMonths(1L).format(dateTimeFormatter));
        endDate = ObjectUtils.defaultIfNull(endDate, LocalDate.now().format(dateTimeFormatter));

        return ResponseEntity.ok(summaryService.perAttedantByPeriod(initDate, endDate));
    }
    @GetMapping("/per-month")
    ResponseEntity<Map<String, List<SummaryDTO>>> getSummaryPerPeriod(){


        return ResponseEntity.ok(summaryService.perMonthOfYear());
    }
}
