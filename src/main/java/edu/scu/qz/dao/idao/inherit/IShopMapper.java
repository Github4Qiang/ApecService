package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.ShopMapper;
import edu.scu.qz.dao.pojo.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IShopMapper extends ShopMapper {

    int selectByIdUserId(@Param("id") Integer id, @Param("producerId") Integer producerId);

    int countByUserId(Integer producerId);

    Shop selectByUserId(Integer producerId);

    Integer countShop();

    List<Shop> selectShopList();

    List<Shop> selectShopListByStatus(Integer status);
}