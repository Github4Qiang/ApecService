package edu.scu.qz.service.impl;

import edu.scu.qz.common.Const;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.ShopMapper;
import edu.scu.qz.dao.idao.inherit.IShopMapper;
import edu.scu.qz.dao.pojo.Shop;
import edu.scu.qz.service.IShopService;
import edu.scu.qz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iShopService")
public class ShopServiceImpl implements IShopService {

    @Autowired
    private IShopMapper shopMapper;
    @Autowired
    private IUserService iUserService;

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
}
