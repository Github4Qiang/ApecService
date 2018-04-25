package edu.scu.qz.controller.backend;

import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IShopService;
import edu.scu.qz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrator/shop/")
public class ShopManageController {


    @Autowired
    private IShopService iShopService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "verifyPass.do")
    @ResponseBody
    public ServerResponse verifyPass(HttpSession session, Integer shopId) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 是否有管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iShopService.verifyPass(shopId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "lock.do")
    @ResponseBody
    public ServerResponse lockShop(HttpSession session, Integer shopId) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 是否有管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iShopService.lockShop(shopId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "unlock.do")
    @ResponseBody
    public ServerResponse unlockShop(HttpSession session, Integer shopId) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 是否有管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iShopService.unlockShop(shopId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getShopList(HttpSession session,
                                      @RequestParam(value = "status", defaultValue = "-1") Integer status,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 是否有管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iShopService.getShopList(status, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    @RequestMapping(value = "get_information.do")
    @ResponseBody
    public ServerResponse getShopInfo(HttpSession session, Integer shopId) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 是否有管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iShopService.getShopInfoByShopId(shopId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
