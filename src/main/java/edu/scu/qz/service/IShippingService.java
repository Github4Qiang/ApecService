package edu.scu.qz.service;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Shipping;

public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse del(Integer id, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse select(Integer id, Integer shippingId);

    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
