package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /*
    * 新增菜品
    * */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 插入菜品表
        dishMapper.insert(dish);
        // 获取菜品id
        Long dishId = dish.getId();
        // 为flavors设置菜品id
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            // 批量插入口味表
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /*
    * 菜品分页查询
    * */

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /*
    * 批量删除菜品信息
    * */
    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {
        // 判断菜品状态，若启售则不能删
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 判断菜品是否关联套餐，若关联则不能删
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 批量删除菜品
        dishMapper.deleteByIds(ids);
        // 批量删除菜品对应的口味信息
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /*
    * 根据id查询菜品信息和对应的口味信息
    * */
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();
        // 根据id查询菜品基本信息
        Dish dish = dishMapper.getById(id);
        // 根据菜品id查询口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        // 封装查询到的信息
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /*
    * 更新菜品信息和其口味信息
    * */

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 更新菜品信息
        dishMapper.update(dish);
        // 删除原有口味信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 添加新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            // 批量插入口味表
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /*
    * 根据分类id查询菜品列表
    * */

    @Override
    public List<DishVO> getByCategoryID(Long categoryId) {
        List<DishVO> dishVOList = dishMapper.getByCategoryID(categoryId);
        return dishVOList;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /*
    * 菜品起售、停售
    * */

    @Override
    public void startOrStop(Integer status, Long id) {
        dishMapper.startOrStop(status, id);
    }
}
