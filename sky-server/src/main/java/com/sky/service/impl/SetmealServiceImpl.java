package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();


        setmeal.setStatus(0);             // 新增的套餐默认为停售状态
        // 插入套餐表
        setmealMapper.insert(setmeal);

        // 获取生成的套餐id
        Long setmealId = setmeal.getId();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));

        // 插入套餐菜品表
        setmealDishes.forEach(setmealDish -> setmealDishMapper.insert(setmealDish));

    }

    /*
    * 分页查询套餐信息
    * */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /*
    * 根据id查询套餐和菜品
    * */

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        // 查询套餐信息
        Setmeal setmeal = setmealMapper.getById(id);
        BeanUtils.copyProperties(setmeal, setmealVO);
        // 查询套餐对应的菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /*
    * 套餐起售、停售
    * */
    @Override
    public void startAndStop(Integer status, Long id) {
        setmealMapper.startAndStop(status,id);
    }
}
