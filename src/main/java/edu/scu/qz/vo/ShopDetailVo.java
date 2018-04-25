package edu.scu.qz.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ShopDetailVo {

    private Integer id;

    private String shopName;

    private BigDecimal balance;

    private Integer shopStatus;

    private String shopStatusDesc;

    private Integer producerId;

    private String producerName;

    private String producerProvince;

    private String producerCity;

    private String producerDistrict;

    private String producerAddress;

    private String serverPhone;

    private String bankCard;

    private String createTime;

    private String updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(Integer shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopStatusDesc() {
        return shopStatusDesc;
    }

    public void setShopStatusDesc(String shopStatusDesc) {
        this.shopStatusDesc = shopStatusDesc;
    }

    public Integer getProducerId() {
        return producerId;
    }

    public void setProducerId(Integer producerId) {
        this.producerId = producerId;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getProducerProvince() {
        return producerProvince;
    }

    public void setProducerProvince(String producerProvince) {
        this.producerProvince = producerProvince;
    }

    public String getProducerCity() {
        return producerCity;
    }

    public void setProducerCity(String producerCity) {
        this.producerCity = producerCity;
    }

    public String getProducerDistrict() {
        return producerDistrict;
    }

    public void setProducerDistrict(String producerDistrict) {
        this.producerDistrict = producerDistrict;
    }

    public String getProducerAddress() {
        return producerAddress;
    }

    public void setProducerAddress(String producerAddress) {
        this.producerAddress = producerAddress;
    }

    public String getServerPhone() {
        return serverPhone;
    }

    public void setServerPhone(String serverPhone) {
        this.serverPhone = serverPhone;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
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
}
