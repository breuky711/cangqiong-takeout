package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;
    /*
     * 营业额统计
     * */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计：{}到{}", begin, end);

        TurnoverReportVO turnoverStatistics = reportService.getTurnoverStatistics(begin,end);
        return Result.success(turnoverStatistics);
    }

    /*
    * 用户统计
    * */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计：{}到{}", begin, end);
        UserReportVO userStatistics = reportService.getUserStatistics(begin,end);
        return Result.success(userStatistics);
    }

    /*
    * 订单统计
    * */
    @GetMapping("/ordersStatistics")
    @ApiOperation("用户统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计：{}到{}", begin, end);
        OrderReportVO orderReportVO = reportService.getOrdersStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    /*
     * 销量排名top10统计
     * */
    @GetMapping("/top10")
    @ApiOperation("销量排名top10统计")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量排名top10统计：{}到{}", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getTop10(begin,end);
        return Result.success(salesTop10ReportVO);
    }
}
