package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*
    * 订单搜索
    * */
    @GetMapping("/conditionSearch")
    @ApiOperation("根据条件查询订单")
    public Result<PageResult> orderSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索，{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.orderSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 各个状态的订单数量统计
    * */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> orderStatusStatistics(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.orderStatusStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 管理端查询订单详情
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        log.info("管理端查询订单详情，id：{}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /*
    * 接单
    * */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result acceptOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单，id：{}", ordersConfirmDTO.getId());
        orderService.acceptOrder(ordersConfirmDTO);
        return Result.success();
    }

    /*
    * 派送订单
    * */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id){
        log.info("派送订单，id：{}", id);
        orderService.deliveryOrder(id);
        return Result.success();
    }
}
