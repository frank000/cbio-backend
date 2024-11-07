package com.cbio.core.service;

import com.cbio.core.v1.dto.SummaryDTO;

import java.util.List;
import java.util.Map;

public interface SummaryService {
    Map<String, List<SummaryDTO>> perAttedantByPeriod(String dateInitString, String dateEndString );
    Map<String, List<SummaryDTO>> perMonthOfYear();
}
