package edu.scu.qz.common;

/**
 * Created by Polylanger on 2017/12/3.
 */
public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),

    NEED_LOGIN(5, "NEED_LOGIN"),

    NEED_SUBMIT_SHOP_INFO(9, "NEED_SUBMIT_SHOP_INFO"),
    WAIT_ADMIN_VERIFY(10, "WAIT_ADMIN_VERIFY"),
    NEED_ACTIVATE(11, "NEED_ACTIVATE");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
