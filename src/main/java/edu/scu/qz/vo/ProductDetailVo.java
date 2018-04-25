package edu.scu.qz.vo;

import java.math.BigDecimal;

public class ProductDetailVo {

    private Integer id;
    private String name;
    private Integer shopId;
    private String shopName;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private Integer status;
    private String statusDesc;
    private Integer stock;
    private String createTime;
    private String updateTime;
    private Integer categoryIdLv1;
    private Integer categoryIdLv2;
    private Integer categoryIdLv3;

    private String imageHost;

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getCategoryIdLv1() {
        return categoryIdLv1;
    }

    public void setCategoryIdLv1(Integer categoryIdLv1) {
        this.categoryIdLv1 = categoryIdLv1;
    }

    public Integer getCategoryIdLv2() {
        return categoryIdLv2;
    }

    public void setCategoryIdLv2(Integer categoryIdLv2) {
        this.categoryIdLv2 = categoryIdLv2;
    }

    public Integer getCategoryIdLv3() {
        return categoryIdLv3;
    }

    public void setCategoryIdLv3(Integer categoryIdLv3) {
        this.categoryIdLv3 = categoryIdLv3;
    }
}
