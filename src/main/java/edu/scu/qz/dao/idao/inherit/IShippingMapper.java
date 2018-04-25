package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.ShippingMapper;
import edu.scu.qz.dao.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IShippingMapper extends ShippingMapper {

    int deleteByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateByShipping(Shipping shipping);

    Shipping selectByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectByUserId(Integer userId);
}