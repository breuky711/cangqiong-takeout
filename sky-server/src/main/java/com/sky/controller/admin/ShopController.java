package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺管理模块相关接口")
@Slf4j
public class ShopController {

    private static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * 修改店铺状态
    * */
    @PutMapping("/{status}")
    @ApiOperation("修改店铺状态")
    public Result setShopStatus(@PathVariable Integer status){
        log.info("修改店铺状态为：{}", status==1 ? "营业中" : "打烊");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /*
    * 获取店铺状态
    * */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getShopStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺状态为：{}", shopStatus==1 ? "营业中" : "打烊");
        return Result.success(shopStatus);
    }

}
