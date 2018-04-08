package edu.scu.qz.service;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Shop;

public interface IShopService {

    ServerResponse save(Integer userId, String username, Shop shop);

    ServerResponse update(Integer userId, Shop shop);

    ServerResponse verifyPass(Integer shopId);

    ServerResponse activateShop(Integer producerId);

    ServerResponse getShopInfo(Integer producerId);
}


