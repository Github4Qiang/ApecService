package edu.scu.qz.controller.backend;

import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IStatisticService;
import edu.scu.qz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrator/statistic/")
public class StatisticManageController {

    @Autowired
    private IStatisticService iStatisticService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "index_data.do")
    @ResponseBody
    public ServerResponse verifyPass(HttpSession session) {
        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 是否有管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iStatisticService.adminIndexData();
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

}
