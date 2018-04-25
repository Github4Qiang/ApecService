package edu.scu.qz.controller.cybershop;

import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IOrderService;
import edu.scu.qz.vo.OrderShopVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/producer/order/")
public class OrderCyberController {

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "send.do")
    @ResponseBody
    public ServerResponse send(HttpSession session, Long subOrderNo) {
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
        return iOrderService.send(user.getId(), subOrderNo);
    }


    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<OrderShopVo> detail(HttpSession session, Long subOrderNo) {
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
        return iOrderService.getSubOrderDetail(user.getId(), subOrderNo);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "status", defaultValue = "-1") Integer status,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
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
        return iOrderService.getSubOrderList(user.getId(), pageNum, pageSize, status);
    }

}
