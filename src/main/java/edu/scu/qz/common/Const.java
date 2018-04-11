package edu.scu.qz.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Polylanger on 2017/12/4.
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role {
        int ROLE_CUSTOMER = 0;  // 普通用户

        int ROLE_APPLICANT = 10;  // 已提交店铺信息的卖家
        int ROLE_CANDIDATE = 11;  // 平台审核成功，待激活的卖家
        int ROLE_PRODUCER = 12;  // 已入驻的卖家

        int ROLE_ADMIN = 20;     // 管理员
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc", "price_desc");
    }

    public interface Cart {
        Integer CHECKED = 1;    // 购物车中商品已选中状态
        Integer UNCHECKED = 0;  // 购物车中商品未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEnum {
        ON_SALE(1, "在线"), TAKE_DOWN(2, "下架"), DELETE(3, "删除");

        private String value;
        private int code;

        ProductStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum OrderStatusEnum {
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已付款"),
        SPLIT(30, "已拆分"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSE(60, "订单关闭");

        private String value;
        private int code;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举: " + code);
        }
    }

    public enum PayPlatformEnum {
        ALIPAY(1, "支付宝");

        private String value;
        private int code;

        PayPlatformEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum {
        ONLINE_PAY(1, "在线支付");

        private String value;
        private int code;

        PaymentTypeEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举: " + code);
        }
    }

    public enum ShopStatus {
        NORMAL(0, "正常"),
        UNVERIFY(1, "待审核"),
        UNACTIVATED(2, "待激活"),
        LOCK(3, "被锁定");

        private String value;
        private int code;

        ShopStatus(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static ShopStatus codeOf(int code) {
            for (ShopStatus shopStatus : values()) {
                if (shopStatus.getCode() == code) {
                    return shopStatus;
                }
            }
            throw new RuntimeException("没有找到对应的枚举: " + code);
        }
    }
}
