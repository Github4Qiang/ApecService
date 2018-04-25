package edu.scu.qz.service;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Shop;

public interface IShopService {

    ServerResponse save(Integer userId, String username, Shop shop);

    ServerResponse update(Integer userId, Shop shop);

    ServerResponse verifyPass(Integer shopId);

    ServerResponse activateShop(Integer producerId);

    ServerResponse unlockShop(Integer shopId);

    ServerResponse getShopInfo(Integer producerId);

    ServerResponse getShopList(Integer status, Integer pageNum, Integer pageSize);

    ServerResponse getShopInfoByShopId(Integer shopId);

    ServerResponse lockShop(Integer shopId);
}


