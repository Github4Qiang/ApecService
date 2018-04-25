package edu.scu.qz.vo;

import java.math.BigDecimal;
import java.util.List;

public class OrderProductVo {

    private List<OrderShopProductVo> orderShopProductVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;

    public List<OrderShopProductVo> getOrderShopProductVoList() {
        return orderShopProductVoList;
    }

    public void setOrderShopProductVoList(List<OrderShopProductVo> orderShopProductVoList) {
        this.orderShopProductVoList = orderShopProductVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
