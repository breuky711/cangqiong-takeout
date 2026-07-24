package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用户订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*
    * 用户下单
    * */
    @PostMapping("/submit")
    @ApiOperation("/用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /*
    * 历史订单查询
    * */
    @GetMapping("/historyOrders")
    @ApiOperation("用户端历史订单查询")
    public Result<PageResult> historyOrderQuery(int page, int pageSize, Integer status){
        log.info("用户端历史订单查询：{}", page, pageSize, status);
        PageResult pageResult = orderService.historyOrdersQuery(page, pageSize, status);
        return Result.success(pageResult);
    }

    /*
    * 根据id查询订单和订单细节
    * */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("用户端查询订单和订单细节")
    public Result<OrderVO> getOrderById(@PathVariable Long id){
        log.info("查询id为{}的订单和订单细节", id);
        OrderVO orderVO = orderService.getOrderById(id);
        return Result.success(orderVO);
    }

    /*
    * 取消订单
    * */
    @PutMapping("/cancel/{id}")
    @ApiOperation("用户取消订单")
    public Result cancel(@PathVariable Long id){
        log.info("用户取消订单：id为{}", id);
        orderService.cancel(id);
        return Result.success();
    }

    /*
    * 再来一单
    * */
    @PostMapping("/repetition/{id}")
    public Result again(@PathVariable Long id){
        log.info("再来一单：id为{}", id);
        orderService.again(id);
        return Result.success();
    }
}
