package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /*
     * 营业额统计
     * */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 该集合用于存放从begin到end的每一天
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        // 查询每一天的营业额
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /*
     * 用户统计
     * */

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            // 先查询总的用户数量
            map.put("end", endTime);
            Integer totalUser = userMapper.getUserByMap(map);
            // 查询新增用户数量
            map.put("begin", beginTime);
            Integer newUser = userMapper.getUserByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ','))
                .build();
    }

    /*
    * 订单统计
    * */

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            // 查询每天全部订单
            Integer totalOrderCount =orderMapper.getOrderByMap(map);
            // 查询每天有效订单
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderByMap(map);

            orderCountList.add(totalOrderCount);
            validOrderCountList.add(validOrderCount);
        }
        // 获取有效时间内总订单数
        Integer totalOrdercount = orderCountList.stream().reduce(Integer::sum).get();
        // 获取有效时间内有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        // 计算订单完成率
        Double orderCompletionRate = totalOrdercount == 0 ? 0.0 : (validOrderCount * 1.0 / totalOrdercount);

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .orderCountList(StringUtils.join(orderCountList, ','))
                .validOrderCountList(StringUtils.join(validOrderCountList, ','))
                .totalOrderCount(totalOrdercount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /*
    * 销量排名top10统计
    * */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);

        List<GoodsSalesDTO> list = orderMapper.getTop10ByMap(map);

        List<String> names = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(names,","))
                .numberList(StringUtils.join(numbers,","))
                .build();
    }
}
