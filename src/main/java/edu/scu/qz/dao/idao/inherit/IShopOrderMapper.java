package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.ShopOrderMapper;
import edu.scu.qz.dao.pojo.ShopOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IShopOrderMapper extends ShopOrderMapper {

    List<ShopOrder> selectByUserIdOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    void batchInsertShopOrder(@Param("shopOrderList") List<ShopOrder> shopOrderList);

    List<ShopOrder> selectByOrderNo(Long orderNo);

    List<ShopOrder> selectByShopId(Integer shopId);

    List<ShopOrder> selectByShopIdStatus(@Param("shopId") Integer shopId, @Param("status") Integer status);

    ShopOrder selectBySubOrderId(Long subOrderNo);
}