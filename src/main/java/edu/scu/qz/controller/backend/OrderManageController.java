package edu.scu.qz.controller.backend;

import com.github.pagehelper.PageInfo;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IOrderService;
import edu.scu.qz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/administrator/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpSession session, Long orderNo) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpSession session, Long orderNo,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "send_goods.do")
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session, Long orderNo) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
