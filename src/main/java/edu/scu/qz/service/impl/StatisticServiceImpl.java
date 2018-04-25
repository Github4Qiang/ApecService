package edu.scu.qz.service.impl;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.inherit.ICategoryMapper;
import edu.scu.qz.dao.idao.inherit.IProductMapper;
import edu.scu.qz.dao.idao.inherit.IShopMapper;
import edu.scu.qz.dao.idao.inherit.IUserMapper;
import edu.scu.qz.service.IStatisticService;
import edu.scu.qz.vo.sdo.AdminIndexSdo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iStatisticService")
public class StatisticServiceImpl implements IStatisticService {

    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private ICategoryMapper categoryMapper;
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private IShopMapper shopMapper;

    @Override
    public ServerResponse<AdminIndexSdo> adminIndexData() {
        AdminIndexSdo adminIndexSdo = new AdminIndexSdo();
        adminIndexSdo.setUserCount(userMapper.countUser());
        adminIndexSdo.setProductCount(productMapper.countProduct());
        adminIndexSdo.setShopCount(shopMapper.countShop());
        adminIndexSdo.setLv1CategoryCount(categoryMapper.countByLevel(1));
        adminIndexSdo.setLv2CategoryCount(categoryMapper.countByLevel(2));
        adminIndexSdo.setLv3CategoryCount(categoryMapper.countByLevel(3));
        return ServerResponse.createBySuccess(adminIndexSdo);
    }

}
