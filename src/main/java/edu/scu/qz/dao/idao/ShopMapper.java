package edu.scu.qz.dao.idao;

import edu.scu.qz.dao.pojo.Shop;
import org.apache.ibatis.annotations.Param;

public interface ShopMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shop record);

    int insertSelective(Shop record);

    Shop selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shop record);

    int updateByPrimaryKey(Shop record);

    int selectByIdUserId(@Param("id") Integer id, @Param("producerId") Integer producerId);

    int countByUserId(Integer producerId);

    Shop selectByUserId(Integer producerId);
}