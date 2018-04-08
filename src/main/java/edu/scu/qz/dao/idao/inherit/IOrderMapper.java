package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.OrderMapper;
import edu.scu.qz.dao.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IOrderMapper extends OrderMapper{

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    Order selectByOrderNo(Long orderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrder();

}