package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
* 自定义超时任务类
* */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    OrderMapper orderMapper;
    /*
    * 每分钟处理一次超时订单
    * */
    @Scheduled(cron="0 * * * * ? ")       // 每分钟执行一次
    public void processTimeoutOrder(){
        log.info("处理超时订单:{}", LocalDateTime.now());
        // 获取待支付状态且超时的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getBystatusAndOrderTimeLt(Orders.PENDING_PAYMENT, time);
        // 将订单状态改为取消
        if (ordersList != null && !ordersList.isEmpty()){
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }

    /*
    * 每天凌晨一点处理派送中的订单
    * */
    @Scheduled(cron="0 0 1 * * ? ")
    public void processDeliveryOrder(){
        log.info("处理派送中的订单:{}", LocalDateTime.now());
        // 获取派送中的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getBystatusAndOrderTimeLt(Orders.DELIVERY_IN_PROGRESS, time);
        // 将订单状态改为完成
        if (ordersList != null && !ordersList.isEmpty()){
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            });
        }
    }
}
