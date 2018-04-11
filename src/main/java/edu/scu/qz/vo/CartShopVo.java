package edu.scu.qz.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartShopVo {

    List<CartProductVo> cartProductVoList;
    private BigDecimal cartShopTotalPrice;  // 店铺中商品的总价
    String shopName;    // 店铺名称
    Integer shopId;     // 店铺ID

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartShopTotalPrice() {
        return cartShopTotalPrice;
    }

    public void setCartShopTotalPrice(BigDecimal cartShopTotalPrice) {
        this.cartShopTotalPrice = cartShopTotalPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }
}
