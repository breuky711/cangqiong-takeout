package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface OrderMapper {

    /*
    * 向订单表插入一条数据
    * */
    void insert(Orders orders);

    List<OrderVO> historyOrdersQuery(Integer status);

    @Select("select * from orders where id = #{id}")
    OrderVO getOrderById(Long id);
}
