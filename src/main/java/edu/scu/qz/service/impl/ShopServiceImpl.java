package edu.scu.qz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.inherit.IProductMapper;
import edu.scu.qz.dao.idao.inherit.IShopMapper;
import edu.scu.qz.dao.pojo.Product;
import edu.scu.qz.dao.pojo.Shop;
import edu.scu.qz.service.IShopService;
import edu.scu.qz.service.IUserService;
import edu.scu.qz.util.DateTimeUtil;
import edu.scu.qz.vo.ShopDetailVo;
import edu.scu.qz.vo.ShopItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iShopService")
public class ShopServiceImpl implements IShopService {

    @Autowired
    private IShopMapper shopMapper;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductMapper productMapper;

    @Override
    public ServerResponse save(Integer userId, String username, Shop shop) {
        if (shop != null && username != null && userId != null) {
            shop.setProducerId(userId);
            shop.setProducerName(username);
            shop.setShopStatus(Const.ShopStatus.UNVERIFY.getCode());
            shop.setShopStatusDesc(Const.ShopStatus.UNVERIFY.getValue());

            // 判断该用户是否已经创建了店铺
            int rowCount = shopMapper.countByUserId(userId);
            if (rowCount > 0) {
                return ServerResponse.createByErrorMessage("您已经创建了店铺");
            } else {
                rowCount = shopMapper.insert(shop);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增店铺成功");
                }
                return ServerResponse.createByErrorMessage("新增店铺失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增店铺信息的参数不正确");
    }


    @Override
    public ServerResponse update(Integer userId, Shop shop) {
        if (shop != null && userId != null) {
            // 判断待修改店铺是不是该 userId 所创建
            int rowCount = shopMapper.selectByIdUserId(shop.getId(), userId);
            if (rowCount > 0) {
                Shop updateShop = new Shop();
                updateShop.setId(shop.getId());
                updateShop.setBankCard(shop.getBankCard());
                updateShop.setProducerAddress(shop.getProducerAddress());
                updateShop.setProducerCity(shop.getProducerCity());
                updateShop.setProducerDistrict(shop.getProducerDistrict());
                updateShop.setProducerProvince(shop.getProducerProvince());
                updateShop.setServerPhone(shop.getServerPhone());
                updateShop.setShopName(shop.getShopName());

                rowCount = shopMapper.updateByPrimaryKeySelective(updateShop);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("更新店铺信息成功");
                }
                return ServerResponse.createByErrorMessage("更新店铺信息失败");
            } else {
                return ServerResponse.createByErrorMessage("不是您自己的店铺，不能修改店铺信息");
            }
        }
        return ServerResponse.createByErrorMessage("更新店铺信息的参数不正确");
    }

    @Override
    public ServerResponse activateShop(Integer producerId) {
        if (producerId == null) {
            return ServerResponse.createByErrorMessage("卖家ID为空");
        }
        // 获取店铺信息
        Shop shop = shopMapper.selectByUserId(producerId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户尚未创建店铺");
        }
        // 判断是否已通过审核（shop.status = UNACTIVATE）
        if (shop.getShopStatus() != Const.ShopStatus.UNACTIVATED.getCode()) {
            return ServerResponse.createByErrorMessage("店铺不是待激活状态: " + shop.getShopStatusDesc());
        }
        // 改变用户角色：候选人 -> 卖家
        ServerResponse response = iUserService.changeRole(shop.getProducerId(), Const.Role.ROLE_PRODUCER);
        if (!response.isSuccess()) {
            return ServerResponse.createByErrorMessage("改变用户角色：候选人 -> 卖家 失败！");
        }
        // 改变店铺状态：待激活 -> 正常
        Shop updateShop = new Shop();
        updateShop.setId(shop.getId());
        updateShop.setShopStatus(Const.ShopStatus.NORMAL.getCode());
        updateShop.setShopStatusDesc(Const.ShopStatus.NORMAL.getValue());
        int rowCount = shopMapper.updateByPrimaryKeySelective(updateShop);
        if (rowCount < 1) {
            return ServerResponse.createByErrorMessage("改变店铺状态：待激活 -> 正常 失败！");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse verifyPass(Integer shopId) {
        if (shopId == null) {
            return ServerResponse.createByErrorMessage("店铺ID为空");
        }
        // 获取店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("店铺ID不存在");
        }
        // 判断是否已经提交过店铺信息（shop.status = UNVERIFY）
        if (shop.getShopStatus() != Const.ShopStatus.UNVERIFY.getCode()) {
            return ServerResponse.createByErrorMessage("店铺不是待审核状态: " + shop.getShopStatusDesc());
        }
        // 改变用户角色：申请人 -> 候选人（等待激活）
        ServerResponse response = iUserService.changeRole(shop.getProducerId(), Const.Role.ROLE_CANDIDATE);
        if (!response.isSuccess()) {
            return ServerResponse.createByErrorMessage("改变用户角色：申请人 -> 候选人 失败！");
        }
        // 改变店铺状态：待审核 -> 待激活
        Shop updateShop = new Shop();
        updateShop.setId(shopId);
        updateShop.setShopStatus(Const.ShopStatus.UNACTIVATED.getCode());
        updateShop.setShopStatusDesc(Const.ShopStatus.UNACTIVATED.getValue());
        int rowCount = shopMapper.updateByPrimaryKeySelective(updateShop);
        if (rowCount < 1) {
            return ServerResponse.createByErrorMessage("改变店铺状态：待审核 -> 待激活 失败！");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse lockShop(Integer shopId) {
        if (shopId == null) {
            return ServerResponse.createByErrorMessage("店铺ID为空");
        }
        // 获取店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("店铺ID不存在");
        }
        if (shop.getShopStatus() != Const.ShopStatus.NORMAL.getCode()) {
            return ServerResponse.createByErrorMessage("只有正常已激活的店铺才能被锁定");
        }
        // 改变店铺状态：正常 -> 锁定
        Shop updateShop = new Shop();
        updateShop.setId(shopId);
        updateShop.setShopStatus(Const.ShopStatus.LOCK.getCode());
        updateShop.setShopStatusDesc(Const.ShopStatus.LOCK.getValue());
        int rowCount = shopMapper.updateByPrimaryKeySelective(updateShop);
        if (rowCount < 1) {
            return ServerResponse.createByErrorMessage("改变店铺状态：正常 -> 锁定 失败！");
        }
        // 改变店铺所售卖商品的状态：... -> 锁定
        List<Product> productList = productMapper.selectByShopId(shopId);
        if (productList != null) {
            for (Product product : productList) {
                Product updateProduct = new Product();
                updateProduct.setId(product.getId());
                // 商品状态： ... -> 锁定
                updateProduct.setStatus(Const.ProductStatusEnum.LOCK.getCode());
                // 原来的状态保存到 oldStatus 中
                updateProduct.setOldStatus(product.getStatus());
                productMapper.updateByPrimaryKeySelective(updateProduct);
            }
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse unlockShop(Integer shopId) {
        if (shopId == null) {
            return ServerResponse.createByErrorMessage("店铺ID为空");
        }
        // 获取店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("店铺ID不存在");
        }
        if (shop.getShopStatus() != Const.ShopStatus.LOCK.getCode()) {
            return ServerResponse.createByErrorMessage("只有被锁定店铺才能被解锁");
        }

        // 改变店铺状态：锁定 -> 正常
        Shop updateShop = new Shop();
        updateShop.setId(shopId);
        updateShop.setShopStatus(Const.ShopStatus.NORMAL.getCode());
        updateShop.setShopStatusDesc(Const.ShopStatus.NORMAL.getValue());
        int rowCount = shopMapper.updateByPrimaryKeySelective(updateShop);
        if (rowCount < 1) {
            return ServerResponse.createByErrorMessage("改变店铺状态：锁定 -> 正常 失败！");
        }
        // 改变店铺所售卖商品的状态：锁定 -> oldStatus
        List<Product> productList = productMapper.selectByShopId(shopId);
        if (productList != null) {
            for (Product product : productList) {
                Product updateProduct = new Product();
                updateProduct.setId(product.getId());
                // 商品状态： 锁定 -> old-status
                updateProduct.setStatus(product.getOldStatus());
                // 原来的状态保存到 oldStatus 中
                updateProduct.setOldStatus(product.getStatus());
                productMapper.updateByPrimaryKeySelective(updateProduct);
            }
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getShopInfo(Integer producerId) {
        if (producerId == null) {
            return ServerResponse.createByErrorMessage("卖家ID为空");
        }
        // 获取店铺信息
        Shop shop = shopMapper.selectByUserId(producerId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户尚未创建店铺");
        }
        // 判断是否已通过审核（shop.status = UNACTIVATE）
        if (shop.getShopStatus() != Const.ShopStatus.NORMAL.getCode() && shop.getShopStatus() != Const.ShopStatus.LOCK.getCode()) {
            return ServerResponse.createByErrorMessage("店铺既不是正常状态又不是锁定状态: " + shop.getShopStatusDesc());
        }
        // 返回店铺信息
        return ServerResponse.createBySuccess(shop);
    }

    @Override
    public ServerResponse getShopList(Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shop> shopList = null;
        if (status < 0) {
            shopList = shopMapper.selectShopList();
        } else {
            shopList = shopMapper.selectShopListByStatus(status);
        }
        List<ShopItemVo> shopItemVoList = Lists.newArrayList();
        for (Shop shop : shopList) {
            ShopItemVo shopItemVo = assembleShopItemVo(shop);
            shopItemVoList.add(shopItemVo);
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(shopList);
        pageResult.setList(shopItemVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse getShopInfoByShopId(Integer shopId) {
        if (shopId == null) {
            return ServerResponse.createByErrorMessage("店铺ID为空");
        }
        // 获取店铺信息
        Shop shop = shopMapper.selectByPrimaryKey(shopId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户尚未创建店铺");
        }
        ShopDetailVo shopDetailVo = assembleShopDetailVo(shop);
        // 返回店铺信息
        return ServerResponse.createBySuccess(shopDetailVo);
    }

    private ShopDetailVo assembleShopDetailVo(Shop shop) {
        ShopDetailVo shopDetailVo = new ShopDetailVo();
        shopDetailVo.setId(shop.getId());
        shopDetailVo.setShopName(shop.getShopName());
        shopDetailVo.setShopStatus(shop.getShopStatus());
        shopDetailVo.setShopStatusDesc(shop.getShopStatusDesc());
        shopDetailVo.setServerPhone(shop.getServerPhone());
        shopDetailVo.setProducerId(shop.getProducerId());
        shopDetailVo.setProducerName(shop.getProducerName());
        shopDetailVo.setProducerProvince(shop.getProducerProvince());
        shopDetailVo.setProducerCity(shop.getProducerCity());
        shopDetailVo.setProducerDistrict(shop.getProducerDistrict());
        shopDetailVo.setProducerAddress(shop.getProducerAddress());
        shopDetailVo.setBalance(shop.getBalance());
        shopDetailVo.setBankCard(shop.getBankCard());
        shopDetailVo.setCreateTime(DateTimeUtil.dateToStr(shop.getCreateTime()));
        shopDetailVo.setUpdateTime(DateTimeUtil.dateToStr(shop.getUpdateTime()));
        return shopDetailVo;
    }

    private ShopItemVo assembleShopItemVo(Shop shop) {
        ShopItemVo shopItemVo = new ShopItemVo();
        shopItemVo.setId(shop.getId());
        shopItemVo.setShopName(shop.getShopName());
        shopItemVo.setShopStatus(shop.getShopStatus());
        shopItemVo.setShopStatusDesc(shop.getShopStatusDesc());
        shopItemVo.setServerPhone(shop.getServerPhone());
        shopItemVo.setProducerId(shop.getProducerId());
        shopItemVo.setProducerName(shop.getProducerName());
        shopItemVo.setBalance(shop.getBalance());
        shopItemVo.setCreateTime(DateTimeUtil.dateToStr(shop.getCreateTime()));
        shopItemVo.setUpdateTime(DateTimeUtil.dateToStr(shop.getUpdateTime()));
        return shopItemVo;
    }
}
