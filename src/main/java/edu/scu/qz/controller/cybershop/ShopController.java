package edu.scu.qz.controller.cybershop;


import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Shop;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IShopService;
import edu.scu.qz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/producer/shop/")
public class ShopController {

    @Autowired
    private IShopService iShopService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse createShop(HttpSession session, Shop shop) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 创建店铺
        ServerResponse serverResponse = iShopService.save(user.getId(), user.getUsername(), shop);
        if (serverResponse.isSuccess()) {
            // 店铺创建成功，修改用户角色 普通玩家 -> 待审核的玩家
            iUserService.changeRole(user.getId(), Const.Role.ROLE_APPLICANT);
        }
        return serverResponse;
    }

    @RequestMapping(value = "activate.do")
    @ResponseBody
    public ServerResponse updateShop(HttpSession session) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        } else if (user.getRole().intValue() == Const.Role.ROLE_CUSTOMER) {
            // 尚未提交申请信息
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_SUBMIT_SHOP_INFO.getCode(),
                    ResponseCode.NEED_SUBMIT_SHOP_INFO.getDesc());
        } else if (user.getRole().intValue() == Const.Role.ROLE_APPLICANT) {
            // 已提交申请，等待管理员审核
            return ServerResponse.createByErrorCodeMessage(ResponseCode.WAIT_ADMIN_VERIFY.getCode(),
                    ResponseCode.WAIT_ADMIN_VERIFY.getDesc());
        } else if (user.getRole().intValue() == Const.Role.ROLE_CANDIDATE) {
            // 管理员审核成功，等待用户激活店铺
            return iShopService.activateShop(user.getId());
        }
        return ServerResponse.createByError();
    }

    @RequestMapping(value = "get_information.do")
    @ResponseBody
    public ServerResponse getShopInfo(HttpSession session) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        } else if (user.getRole().intValue() == Const.Role.ROLE_CUSTOMER) {
            // 尚未提交申请信息
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_SUBMIT_SHOP_INFO.getCode(),
                    ResponseCode.NEED_SUBMIT_SHOP_INFO.getDesc());
        } else if (user.getRole().intValue() == Const.Role.ROLE_APPLICANT) {
            // 已提交申请，等待管理员审核
            return ServerResponse.createByErrorCodeMessage(ResponseCode.WAIT_ADMIN_VERIFY.getCode(),
                    ResponseCode.WAIT_ADMIN_VERIFY.getDesc());
        } else if (user.getRole().intValue() == Const.Role.ROLE_CANDIDATE) {
            // 管理员审核成功，等待用户激活店铺
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_ACTIVATE.getCode(),
                    ResponseCode.NEED_ACTIVATE.getDesc());
        }
        // 已经成为卖家 12
        return iShopService.getShopInfo(user.getId());
    }

    @RequestMapping(value = "update.do")
    @ResponseBody
    public ServerResponse updateShop(HttpSession session, Shop shop) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        } else if (user.getRole().intValue() == Const.Role.ROLE_CUSTOMER) {
            // 尚未提交申请信息
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_SUBMIT_SHOP_INFO.getCode(),
                    ResponseCode.NEED_SUBMIT_SHOP_INFO.getDesc());
        } else if (user.getRole().intValue() == Const.Role.ROLE_APPLICANT) {
            // 已提交申请，等待管理员审核
            return ServerResponse.createByErrorCodeMessage(ResponseCode.WAIT_ADMIN_VERIFY.getCode(),
                    ResponseCode.WAIT_ADMIN_VERIFY.getDesc());
        } else if (user.getRole().intValue() == Const.Role.ROLE_CANDIDATE) {
            // 管理员审核成功，等待用户激活店铺
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_ACTIVATE.getCode(),
                    ResponseCode.NEED_ACTIVATE.getDesc());
        }
        // 已经成为卖家 12
        return iShopService.update(user.getId(), shop);
    }

}
