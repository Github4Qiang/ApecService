package edu.scu.qz.controller.cybershop;

import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/producer/user/")
public class ProducerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "get_producer_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getProducerInfo(HttpSession session) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("卖家未登录，无法获得卖家当前信息");
        }
        // 是否已申请为店主：是，则返回店铺 id
        return iUserService.checkProducerRole(user);
    }

    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
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
        return ServerResponse.createBySuccess(user);
    }

}
