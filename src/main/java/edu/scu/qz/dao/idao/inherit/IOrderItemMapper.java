package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.OrderItemMapper;
import edu.scu.qz.dao.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IOrderItemMapper extends OrderItemMapper {

    List<OrderItem> getByUserIdOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    void batchInsertOrderItem(@Param("orderItemList") List<OrderItem> orderItemList);

    List<OrderItem> getByOrderNo(Long orderNo);

    List<OrderItem> getByUserIdSubOrderNo(@Param("userId") Integer userId, @Param("subOrderNo") Long subOrderNo);

    List<OrderItem> getBySubOrderNo(Long subOrderNo);
}