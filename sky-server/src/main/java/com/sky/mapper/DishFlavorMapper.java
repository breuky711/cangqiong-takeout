package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /*
    * 批量插入口味数据
    * */
    void insertBatch(List<DishFlavor> flavors);

    /*
    * 根据菜品信息批量删除口味信息
    * */
    void deleteByDishIds(List<Long> dishIds);

    /*
    * 根据菜品id查询口味信息
    * */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);

    /*
    * 根据菜品id删除口味信息
    * */
    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);
}
