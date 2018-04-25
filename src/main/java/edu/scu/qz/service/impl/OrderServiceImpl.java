package edu.scu.qz.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.inherit.*;
import edu.scu.qz.dao.pojo.*;
import edu.scu.qz.service.IOrderService;
import edu.scu.qz.util.BigDecimalUtil;
import edu.scu.qz.util.DateTimeUtil;
import edu.scu.qz.util.FTPUtil;
import edu.scu.qz.util.PropertiesUtil;
import edu.scu.qz.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IShopOrderMapper shopOrderMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;
    @Autowired
    private IPayInfoMapper payInfoMapper;
    @Autowired
    private ICartMapper cartMapper;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private IShippingMapper shippingMapper;
    @Autowired
    private IShopMapper shopMapper;

    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("APEC扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.getByUserIdOrderNo(userId, orderNo);
        for (OrderItem orderItem : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 根据订单号生成文件路径及文件名
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());

                // 在对应文件路径创建二维码图片
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                // 二维码图片上传至FTP服务器
                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                    // 上传完毕，删除 upload 文件夹下文件
                    targetFile.delete();
                } catch (IOException e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("qrPath:" + qrPath);

                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");

        }
    }

    @Override
    public ServerResponse alipayCallback(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("非APEC中订单，回调忽略");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess("支付宝重复调用");
        }

        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals((tradeStatus))) {
            // 更新总订单中信息
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
            // 更新店铺订单中信息
            List<ShopOrder> shopOrderList = shopOrderMapper.selectByUserIdOrderNo(order.getUserId(), orderNo);
            if (shopOrderList != null) {
                for (ShopOrder shopOrder : shopOrderList) {
                    ShopOrder updateShopOrder = new ShopOrder();
                    updateShopOrder.setId(shopOrder.getId());
                    updateShopOrder.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
                    updateShopOrder.setStatus(Const.OrderStatusEnum.PAID.getCode());
                    shopOrderMapper.updateByPrimaryKeySelective(updateShopOrder);
                }
            }
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }


    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        Long orderNo = generateOrderNo();
        // 从购物车中获取所有被勾选的商品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        List<OrderItem> orderItemList = Lists.newArrayList();
        Map<Integer, ShopOrder> shopOrderMap = Maps.newHashMap();
        Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        ServerResponse response = produceShopOrder(shippingId, orderNo, cartList, orderItemList, shopOrderMap, orderItemListMap);
        if (!response.isSuccess()) return response;

        List<ShopOrder> shopOrderList = Lists.newArrayList(shopOrderMap.values());

        // 计算这个订单的总价
        BigDecimal payment = getOrderTotalPrice(shopOrderList);
        // 生成订单
        Order order = assembleOrder(userId, shippingId, payment, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单生成失败");
        }

        shopOrderMapper.batchInsertShopOrder(shopOrderList);
        orderItemMapper.batchInsertOrderItem(orderItemList);
        // 订单生成成功，减少商品库存
        reduceProductStock(orderItemList);
        // 清空购物车
        cleanCart(cartList);
        // 返回给前端的数据
        OrderVo orderVo = assembleOrderVo(order, shopOrderList, orderItemListMap);
        return ServerResponse.createBySuccess(orderVo);
    }

    private ServerResponse produceShopOrder(Integer shippingId, Long orderNo, List<Cart> cartList, List<OrderItem> orderItemList, Map<Integer, ShopOrder> shopOrderMap, Map<Integer, List<OrderItem>> orderItemListMap) {
        // 校验购物车中产品的状态和数量
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            // 校验是否已经下架
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品【" + product.getName() + "】不是在线售卖状态");
            }
            // 校验库存
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品【" + product.getName() + "】库存不足");
            }
            // 构建 orderItem
            orderItem.setUserId(cartItem.getUserId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cartItem.getQuantity()));
            // 构建 shopOrder
            ShopOrder shopOrder = null;
            if (shopOrderMap.containsKey(product.getShopId())) {
                orderItemListMap.get(product.getShopId()).add(orderItem);

                shopOrder = shopOrderMap.get(product.getShopId());
                // 店铺总金额 += orderItem.payment
                shopOrder.setPayment(BigDecimalUtil.add(shopOrder.getPayment().doubleValue(), orderItem.getTotalPrice().doubleValue()));
            } else {
                orderItemListMap.put(product.getShopId(), Lists.newArrayList(orderItem));

                shopOrder = new ShopOrder();
                shopOrder.setSubOrderNo(generateOrderNo());     // 生成子订单表
                shopOrder.setOrderNo(orderNo);
                shopOrder.setUserId(cartItem.getUserId());
                shopOrder.setShippingId(shippingId);            // 设置收货地址ID
                shopOrder.setShopId(product.getShopId());
                shopOrder.setShopName(product.getShopName());
                shopOrder.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
                shopOrder.setPayment(orderItem.getTotalPrice());
                shopOrder.setPostage(0);
                shopOrder.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
                shopOrderMap.put(product.getShopId(), shopOrder);
            }
            orderItem.setSubOrderNo(shopOrder.getSubOrderNo());
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户此订单不存在");
        }
        // TODO : 退款
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("已付款，无法取消");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row < 0) {
            return ServerResponse.createByError();
        }
        List<ShopOrder> shopOrderList = shopOrderMapper.selectByUserIdOrderNo(userId, orderNo);
        for (ShopOrder shopOrder : shopOrderList) {
            ShopOrder updateShopOrder = new ShopOrder();
            updateShopOrder.setId(shopOrder.getId());
            updateShopOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
            row = shopOrderMapper.updateByPrimaryKeySelective(updateShopOrder);
            if (row < 0) return ServerResponse.createByError();
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
        // 从购物车中获取所有被勾选的商品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        List<OrderItem> orderItemList = Lists.newArrayList();
        Map<Integer, ShopOrder> shopOrderMap = Maps.newHashMap();
        Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        ServerResponse response = produceShopOrder(null, null, cartList, orderItemList, shopOrderMap, orderItemListMap);
        if (!response.isSuccess()) return response;

        List<ShopOrder> shopOrderList = Lists.newArrayList(shopOrderMap.values());
        List<OrderShopProductVo> orderShopProductVoList = Lists.newArrayList();
        // 计算这个订单的总价
        BigDecimal payment = getOrderTotalPrice(shopOrderList);

        OrderProductVo orderProductVo = new OrderProductVo();

        for (Integer shopId : shopOrderMap.keySet()) {
            OrderShopProductVo orderShopProductVo = assembleOrderShopProductVo(shopOrderMap.get(shopId), orderItemListMap.get(shopId));
            orderShopProductVoList.add(orderShopProductVo);
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderShopProductVoList(orderShopProductVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order != null) {
            List<ShopOrder> shopOrderList = shopOrderMapper.selectByUserIdOrderNo(userId, orderNo);
            Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
            for (ShopOrder shopOrder : shopOrderList) {
                List<OrderItem> orderItemList = orderItemMapper.getByUserIdSubOrderNo(userId, shopOrder.getSubOrderNo());
                orderItemListMap.put(shopOrder.getShopId(), orderItemList);
            }
            OrderVo orderVo = assembleOrderVo(order, shopOrderList, orderItemListMap);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    @Override
    public ServerResponse getOrderList(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<ShopOrder> shopOrderList = shopOrderMapper.selectByUserIdOrderNo(userId, order.getOrderNo());
            Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
            for (ShopOrder shopOrder : shopOrderList) {
                List<OrderItem> orderItemList = orderItemMapper.getByUserIdSubOrderNo(userId, shopOrder.getSubOrderNo());
                orderItemListMap.put(shopOrder.getShopId(), orderItemList);
            }
            OrderVo orderVo = assembleOrderVo(order, shopOrderList, orderItemListMap);
            orderVoList.add(orderVo);
        }

        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> manageList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();

        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<ShopOrder> shopOrderList = shopOrderMapper.selectByOrderNo(order.getOrderNo());
            Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
            for (ShopOrder shopOrder : shopOrderList) {
                List<OrderItem> orderItemList = orderItemMapper.getBySubOrderNo(shopOrder.getSubOrderNo());
                orderItemListMap.put(shopOrder.getShopId(), orderItemList);
            }
            OrderVo orderVo = assembleOrderVo(order, shopOrderList, orderItemListMap);
            orderVoList.add(orderVo);
        }

        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<ShopOrder> shopOrderList = shopOrderMapper.selectByOrderNo(orderNo);
            Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
            for (ShopOrder shopOrder : shopOrderList) {
                List<OrderItem> orderItemList = orderItemMapper.getBySubOrderNo(shopOrder.getSubOrderNo());
                orderItemListMap.put(shopOrder.getShopId(), orderItemList);
            }
            OrderVo orderVo = assembleOrderVo(order, shopOrderList, orderItemListMap);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    @Override
    public ServerResponse manageSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<ShopOrder> shopOrderList = shopOrderMapper.selectByOrderNo(orderNo);
            Map<Integer, List<OrderItem>> orderItemListMap = Maps.newHashMap();
            for (ShopOrder shopOrder : shopOrderList) {
                List<OrderItem> orderItemList = orderItemMapper.getBySubOrderNo(shopOrder.getSubOrderNo());
                orderItemListMap.put(shopOrder.getShopId(), orderItemList);
            }
            OrderVo orderVo = assembleOrderVo(order, shopOrderList, orderItemListMap);

            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    @Override
    public ServerResponse manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    @Override
    public ServerResponse getSubOrderList(Integer userId, Integer pageNum, Integer pageSize, Integer status) {
        Shop shop = shopMapper.selectByUserId(userId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户未开通店铺");
        }
        List<ShopOrder> shopOrderList;
        PageHelper.startPage(pageNum, pageSize);
        if (status < 0) {
            shopOrderList = shopOrderMapper.selectByShopId(shop.getId());
        } else {
            shopOrderList = shopOrderMapper.selectByShopIdStatus(shop.getId(), status);
        }
        List<OrderShopProductVo> orderShopProductVoList = Lists.newArrayList();
        for (ShopOrder shopOrder : shopOrderList) {
            List<OrderItem> orderItemList = orderItemMapper.getBySubOrderNo(shopOrder.getSubOrderNo());
            OrderShopProductVo orderShopProductVo = assembleOrderShopProductVo(shopOrder, orderItemList);
            orderShopProductVoList.add(orderShopProductVo);
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(shopOrderList);
        // 将 PageInfo 中数据换成 ProductItemVo-List
        pageResult.setList(orderShopProductVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<OrderShopVo> getSubOrderDetail(Integer userId, Long subOrderNo) {
        ShopOrder shopOrder = shopOrderMapper.selectBySubOrderId(subOrderNo);
        if (shopOrder == null) {
            return ServerResponse.createByErrorMessage("子订单不存在: " + subOrderNo);
        }
        Shop shop = shopMapper.selectByUserId(userId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户未开通店铺");
        } else if (shopOrder.getShopId() != shop.getId()) {
            return ServerResponse.createByErrorMessage("不是该用户创建的子订单");
        }

        List<OrderItem> orderItemList = orderItemMapper.getBySubOrderNo(shopOrder.getSubOrderNo());

        OrderShopVo orderShopVo = assembleOrderShopVo(shopOrder, orderItemList);
        return ServerResponse.createBySuccess(orderShopVo);
    }

    @Override
    public ServerResponse send(Integer userId, Long subOrderNo) {
        ShopOrder shopOrder = shopOrderMapper.selectBySubOrderId(subOrderNo);
        if (shopOrder == null) {
            return ServerResponse.createByErrorMessage("子订单不存在: " + subOrderNo);
        }
        Shop shop = shopMapper.selectByUserId(userId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户未开通店铺");
        } else if (shopOrder.getShopId() != shop.getId()) {
            return ServerResponse.createByErrorMessage("不是该用户创建的子订单");
        }

        ShopOrder updateShopOrder = new ShopOrder();
        updateShopOrder.setId(shopOrder.getId());
        updateShopOrder.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
        int rowCount = shopOrderMapper.updateByPrimaryKeySelective(updateShopOrder);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("设置已发货失败");
    }

    private OrderVo assembleOrderVo(Order order, List<ShopOrder> shopOrderList, Map<Integer, List<OrderItem>> orderItemListMap) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        // 组装收货地址: Shipping
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        // 组装子订单 View-Object 列表
        List<OrderShopProductVo> orderShopProductVoList = Lists.newArrayList();
        for (ShopOrder shopOrder : shopOrderList) {
            OrderShopProductVo orderShopProductVo = assembleOrderShopProductVo(shopOrder, orderItemListMap.get(shopOrder.getShopId()));
            orderShopProductVoList.add(orderShopProductVo);
        }
        orderVo.setOrderShopProductVoList(orderShopProductVoList);
        return orderVo;
    }


    private OrderShopVo assembleOrderShopVo(ShopOrder shopOrder, List<OrderItem> orderItemList) {
        OrderShopVo orderShopVo = new OrderShopVo();
        orderShopVo.setShopId(shopOrder.getShopId());
        orderShopVo.setShopName(shopOrder.getShopName());
        orderShopVo.setPaymentType(shopOrder.getPaymentType());
        orderShopVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(shopOrder.getPaymentType()).getValue());
        orderShopVo.setStatus(shopOrder.getStatus());
        orderShopVo.setStatusDesc(Const.OrderStatusEnum.codeOf(shopOrder.getStatus()).getValue());
        orderShopVo.setSubOrderNo(shopOrder.getSubOrderNo());
        orderShopVo.setPayment(shopOrder.getPayment());
        orderShopVo.setPaymentTime(DateTimeUtil.dateToStr(shopOrder.getPaymentTime()));
        orderShopVo.setSendTime(DateTimeUtil.dateToStr(shopOrder.getSendTime()));
        orderShopVo.setEndTime(DateTimeUtil.dateToStr(shopOrder.getEndTime()));
        orderShopVo.setCloseTime(DateTimeUtil.dateToStr(shopOrder.getCloseTime()));
        orderShopVo.setCreateTime(DateTimeUtil.dateToStr(shopOrder.getCreateTime()));
        orderShopVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        // 组装收货地址: Shipping
        orderShopVo.setShippingId(shopOrder.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(shopOrder.getShippingId());
        if (shipping != null) {
            orderShopVo.setReceiverName(shipping.getReceiverName());
            orderShopVo.setShippingVo(assembleShippingVo(shipping));
        }

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderShopVo.setOrderItemVoList(orderItemVoList);
        return orderShopVo;
    }

    private OrderShopProductVo assembleOrderShopProductVo(ShopOrder shopOrder, List<OrderItem> orderItemList) {
        OrderShopProductVo orderShopProductVo = new OrderShopProductVo();
        orderShopProductVo.setShopOrderId(shopOrder.getId());
        orderShopProductVo.setShopId(shopOrder.getShopId());
        orderShopProductVo.setShopName(shopOrder.getShopName());
        orderShopProductVo.setPaymentType(shopOrder.getPaymentType());
        orderShopProductVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(shopOrder.getPaymentType()).getValue());
        orderShopProductVo.setStatus(shopOrder.getStatus());
        orderShopProductVo.setStatusDesc(Const.OrderStatusEnum.codeOf(shopOrder.getStatus()).getValue());
        orderShopProductVo.setSubOrderNo(shopOrder.getSubOrderNo());
        orderShopProductVo.setPayment(shopOrder.getPayment());
        orderShopProductVo.setPaymentTime(DateTimeUtil.dateToStr(shopOrder.getPaymentTime()));
        orderShopProductVo.setSendTime(DateTimeUtil.dateToStr(shopOrder.getSendTime()));
        orderShopProductVo.setEndTime(DateTimeUtil.dateToStr(shopOrder.getEndTime()));
        orderShopProductVo.setCloseTime(DateTimeUtil.dateToStr(shopOrder.getCloseTime()));
        orderShopProductVo.setCreateTime(DateTimeUtil.dateToStr(shopOrder.getCreateTime()));
        orderShopProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderShopProductVo.setOrderItemVoList(orderItemVoList);
        return orderShopProductVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        return shippingVo;
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    // 清空购物车
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    // 减少商品库存
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    // 生成订单
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment, Long orderNo) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        // payment_time | send_time | end_time | close_time 待对应操作发生后添加

        int rowCount = orderMapper.insert(order);
        if (rowCount > 0)
            return order;
        return null;
    }

    // 生成订单号
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    // 计算这个订单的总价: sum(shopOrder.payment)
    private BigDecimal getOrderTotalPrice(List<ShopOrder> shopOrderList) {
        BigDecimal payment = BigDecimalUtil.newBigDecimalZero();
        for (ShopOrder shopOrder : shopOrderList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), shopOrder.getPayment().doubleValue());
        }
        return payment;
    }

}
