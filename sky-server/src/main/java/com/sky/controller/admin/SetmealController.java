package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐管理模块")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /*
    * 新增套餐
    * */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /*
    * 条件分页查询套餐信息
    * */
    @GetMapping("/page")
    @ApiOperation("条件分页查询套餐信息")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询套餐信息");
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /*
    * 根据id查询套餐和菜品
    * */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐和菜品")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐和菜品：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /*
    * 套餐起售、停售
    * */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售")
    public Result startAndStop(@PathVariable Integer status, Long id){
        setmealService.startAndStop(status, id);
        return Result.success();
    }

    /*
    * 批量删除套餐
    * */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteByIds(@RequestParam List<Long> ids){
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteByIds(ids);
        return Result.success();
    }

    /*
    * 修改套餐信息
    * */
    @PutMapping
    @ApiOperation("修改套餐信息")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐信息：{}", setmealDTO);
        setmealService.updateWithSetmealDishes(setmealDTO);
        return Result.success();
    }
}
