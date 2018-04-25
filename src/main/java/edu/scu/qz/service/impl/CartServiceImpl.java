package edu.scu.qz.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.ProductMapper;
import edu.scu.qz.dao.idao.inherit.ICartMapper;
import edu.scu.qz.dao.pojo.Cart;
import edu.scu.qz.dao.pojo.Product;
import edu.scu.qz.service.ICartService;
import edu.scu.qz.util.BigDecimalUtil;
import edu.scu.qz.util.PropertiesUtil;
import edu.scu.qz.vo.CartProductVo;
import edu.scu.qz.vo.CartShopVo;
import edu.scu.qz.vo.CartVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private ICartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            // 新增产品
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else {
            // 修改产品数量: 该产品已存在，数量相加
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> delete(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnselect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        Map<Integer, CartShopVo> cartShopVoMap = Maps.newHashMap();
        BigDecimal cartTotalPrice = BigDecimalUtil.newBigDecimalZero();

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartShopVo cartShopVo = null;
                List<CartProductVo> cartProductVoList = null;

                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {     // 库存充足
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        buyLimitCount = cartItem.getQuantity();
                    } else {                                                // 库存不足
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // 更新数据库中该购物车商品的数量，值为库存总量（因为原数量已经大于库存总量）
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    // 计算该商品总价：price * quantity
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), buyLimitCount));
                    cartProductVo.setProductChecked(cartItem.getChecked());

                    if (cartShopVoMap.containsKey(product.getShopId())) {
                        cartShopVo = cartShopVoMap.get(product.getShopId());
                        cartProductVoList = cartShopVo.getCartProductVoList();
                    } else {
                        cartShopVo = new CartShopVo();
                        cartProductVoList = Lists.newArrayList();

                        cartShopVo.setShopName(product.getShopName());
                        cartShopVo.setShopId(product.getShopId());
                        cartShopVo.setCartProductVoList(cartProductVoList);
                        cartShopVoMap.put(product.getShopId(), cartShopVo);
                    }
                    cartProductVoList.add(cartProductVo);

                    // 如果该商品已被勾选，则加入到整个购物车总价/店铺商品总价中
                    if (cartItem.getChecked() == Const.Cart.CHECKED) {
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                        // 计算 cartShopVo 中的 shopTotalPrice: 为空则赋值 0
                        BigDecimal shopTotalPrice = cartShopVo.getCartShopTotalPrice() == null ? BigDecimalUtil.newBigDecimalZero() : cartShopVo.getCartShopTotalPrice();
                        shopTotalPrice = BigDecimalUtil.add(shopTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                        cartShopVo.setCartShopTotalPrice(shopTotalPrice);
                    }
                }
            }
        }
        cartVo.setCartShopVoList(Lists.newArrayList(cartShopVoMap.values()));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.apec.com/"));
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        return cartVo;
    }

    private Boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

}
