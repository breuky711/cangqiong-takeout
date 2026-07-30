package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /*
    * 营业额统计
    * */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
