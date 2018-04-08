package edu.scu.qz.dao.pojo;

import java.util.Date;

public class Shop {
    private Integer id;

    private String shopName;

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

    private Date createTime;

    private Date updateTime;

    public Shop(Integer id, String shopName, Integer shopStatus, String shopStatusDesc, Integer producerId, String producerName, String producerProvince, String producerCity, String producerDistrict, String producerAddress, String serverPhone, String bankCard, Date createTime, Date updateTime) {
        this.id = id;
        this.shopName = shopName;
        this.shopStatus = shopStatus;
        this.shopStatusDesc = shopStatusDesc;
        this.producerId = producerId;
        this.producerName = producerName;
        this.producerProvince = producerProvince;
        this.producerCity = producerCity;
        this.producerDistrict = producerDistrict;
        this.producerAddress = producerAddress;
        this.serverPhone = serverPhone;
        this.bankCard = bankCard;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Shop() {
        super();
    }

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
        this.shopName = shopName == null ? null : shopName.trim();
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
        this.shopStatusDesc = shopStatusDesc == null ? null : shopStatusDesc.trim();
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
        this.producerName = producerName == null ? null : producerName.trim();
    }

    public String getProducerProvince() {
        return producerProvince;
    }

    public void setProducerProvince(String producerProvince) {
        this.producerProvince = producerProvince == null ? null : producerProvince.trim();
    }

    public String getProducerCity() {
        return producerCity;
    }

    public void setProducerCity(String producerCity) {
        this.producerCity = producerCity == null ? null : producerCity.trim();
    }

    public String getProducerDistrict() {
        return producerDistrict;
    }

    public void setProducerDistrict(String producerDistrict) {
        this.producerDistrict = producerDistrict == null ? null : producerDistrict.trim();
    }

    public String getProducerAddress() {
        return producerAddress;
    }

    public void setProducerAddress(String producerAddress) {
        this.producerAddress = producerAddress == null ? null : producerAddress.trim();
    }

    public String getServerPhone() {
        return serverPhone;
    }

    public void setServerPhone(String serverPhone) {
        this.serverPhone = serverPhone == null ? null : serverPhone.trim();
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard == null ? null : bankCard.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}