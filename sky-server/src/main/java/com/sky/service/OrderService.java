package com.sky.service;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 用户端订单分页查询
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     */
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     */
    void repetition(Long id);

    PageResult orderSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO orderStatusStatistics();

    void acceptOrder(OrdersConfirmDTO ordersConfirmDTO);

    void deliveryOrder(Long id);
}
