package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.CartMapper;
import edu.scu.qz.dao.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ICartMapper extends CartMapper {

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    int checkedOrUncheckedProduct(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}