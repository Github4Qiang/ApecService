package edu.scu.qz.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {

    List<CartShopVo> cartShopVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imageHost;

    public List<CartShopVo> getCartShopVoList() {
        return cartShopVoList;
    }

    public void setCartShopVoList(List<CartShopVo> cartShopVoList) {
        this.cartShopVoList = cartShopVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
