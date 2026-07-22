package com.sky.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /*
     * 新增套餐及套餐菜品信息
     * */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();


        setmeal.setStatus(StatusConstant.DISABLE);             // 新增的套餐默认为停售状态
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
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
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
        if (status == StatusConstant.ENABLE) {    // 检查套餐内是否有停售菜品
            // 根据套餐id获得菜品id
            List<Long> dishIds = setmealDishMapper.getDishIdBySetmealId(id);
            // 查询该套餐中处于停售状态的菜品数
            dishIds.forEach(dishId -> {
                int dishStatus = dishMapper.getById(dishId).getStatus();
                if (dishStatus == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }

        setmealMapper.startAndStop(status, id);
    }

    /*
     * 根据id批量删除套餐
     * */

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // 处于起售状态无法删除
        int count = setmealMapper.countByIds(ids);   // 查询处于起售状态且被选中删除的套餐数量
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        // 删除套餐
        setmealMapper.deleteByIds(ids);
        // 删除套餐菜品信息
        ids.forEach(id -> setmealDishMapper.deleteBySetmealId(id));
    }

    /*
     * 修改套餐信息及套餐菜品信息
     * */

    @Override
    @Transactional
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

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
