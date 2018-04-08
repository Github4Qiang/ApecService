package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.ProductMapper;
import edu.scu.qz.dao.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IProductMapper extends ProductMapper {
    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName") String productName, @Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName") String productName, @Param("categoryIdList") List<Integer> categoryIdList);

}