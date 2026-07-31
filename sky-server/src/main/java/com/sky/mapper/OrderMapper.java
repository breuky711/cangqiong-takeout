package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 向订单表插入一条数据
     */
    void insert(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 更新订单
     */
    void update(Orders orders);

    /**
     * 根据订单状态查询数量
     *
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    int countByStatus(Integer status);

    /**
     * 根据订单状态和下单时间查询
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getBystatusAndOrderTimeLt(Integer status, LocalDateTime orderTime);

    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer getOrderByMap(Map map);

    List<GoodsSalesDTO> getTop10ByMap(Map map);
}
