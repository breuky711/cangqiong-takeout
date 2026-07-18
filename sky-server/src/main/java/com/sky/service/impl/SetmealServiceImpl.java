package com.sky.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
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

    /*
     * 新增套餐及套餐菜品信息
     * */
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
        setmealMapper.startAndStop(status, id);
    }

    /*
     * 根据id批量删除套餐
     * */

    @Override
    public void deleteByIds(List<Long> ids) {
        // 处于起售状态无法删除
        int count = setmealMapper.countByIds(ids);   // 查询处于起售状态且被选中删除的套餐数量
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        setmealMapper.deleteByIds(ids);
    }

    /*
     * 修改套餐信息及套餐菜品信息
     * */

    @Override
    public void updateWithSetmealDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();  // 获取修改的套餐菜品信息

        // 修改套餐信息
        setmealMapper.update(setmeal);

        // 修改套餐菜品信息
          // 1. 删除原有套餐菜品信息
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
          // 2. 添加新的套餐菜品信息(参考上面的新增套餐菜品信息的代码)
            // 获取生成的套餐id
        Long setmealId = setmeal.getId();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            // 插入套餐菜品表
        setmealDishes.forEach(setmealDish -> setmealDishMapper.insert(setmealDish));
    }
}
