package edu.scu.qz.service;

import com.github.pagehelper.PageInfo;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.vo.OrderShopVo;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse getOrderDetail(Integer userId, Long orderNo);

    ServerResponse getOrderList(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> manageList(Integer pageNum, Integer pageSize);

    ServerResponse manageDetail(Long orderNo);

    ServerResponse manageSearch(Long orderNo, Integer pageNum, Integer pageSize);

    ServerResponse manageSendGoods(Long orderNo);

    ServerResponse getSubOrderList(Integer userId, Integer pageNum, Integer pageSize, Integer status);

    ServerResponse<OrderShopVo> getSubOrderDetail(Integer userId, Long subOrderNo);

    ServerResponse send(Integer userId, Long subOrderNo);
}
