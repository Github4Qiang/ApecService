package edu.scu.qz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.common.TokenCache;
import edu.scu.qz.dao.idao.inherit.IShopMapper;
import edu.scu.qz.dao.idao.inherit.IUserMapper;
import edu.scu.qz.dao.pojo.Shop;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IUserService;
import edu.scu.qz.util.DateTimeUtil;
import edu.scu.qz.util.MD5Util;
import edu.scu.qz.vo.UserItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IShopMapper shopMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        // 设置最后一次登录时间
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(new Date());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount <= 0) {
            return ServerResponse.createByErrorMessage("设置最后一次登录时间错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    /**
     * 校验用户名或邮箱是否存在
     *
     * @param str：用户名或邮箱
     * @param type：str类型，Const.USERNAME/Const.EMAIL
     * @return Error:已存在；Success:不存在
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误：" + type);
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> response = checkValid(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) {
            return response;
        }
        response = checkValid(user.getEmail(), Const.EMAIL);
        if (!response.isSuccess()) {
            return response;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        // MD5 加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse response = checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // 说明该问题及问题答案是正确的，并且是属于该用户名
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");

    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，需要传递 Token");
        }
        ServerResponse response = checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            // 用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token 无效或过期");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        // 防止横向越权，校检用户ID及旧密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        // 校验 email：新的 email 不能存在；或者存在但属于当前用户
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("Email已存在，请更换email再尝试更新");
        }
        // 只会更新以下字段
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse checkApplicantRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_APPLICANT) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse checkCandidateRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_CANDIDATE) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse checkProducerRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_PRODUCER) {
            // 已经成为卖家 12，返回店铺 id
            Shop shop = shopMapper.selectByUserId(user.getId());
            if (shop != null) {
                return ServerResponse.createBySuccess(shop.getId());
            } else {
                return ServerResponse.createByErrorMessage("没有找到该用户创建的店铺");
            }
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse changeRole(Integer id, int roleApplicant) {
        if (id != null) {
            User user = new User();
            user.setId(id);
            user.setRole(roleApplicant);
            int rowCount = userMapper.updateByPrimaryKeySelective(user);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess();
            }
        }
        return ServerResponse.createByErrorMessage("更改用户角色失败");
    }

    @Override
    public ServerResponse getUserList(Integer pageNum, Integer pageSize, Integer role) {
        List<User> userList;
        PageHelper.startPage(pageNum, pageSize);
        if (role < 0) {
            userList = userMapper.selectUserList();
        } else if (role == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("管理员信息不对外开放！");
        } else {
            userList = userMapper.selectUserListByRole(role);
        }
        List<UserItemVo> userItemVoList = Lists.newArrayList();
        for (User user : userList) {
            UserItemVo userItemVo = assembleUserItemVo(user);
            userItemVoList.add(userItemVo);
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(userList);
        // 将 PageInfo 中数据换成 ProductItemVo-List
        pageResult.setList(userItemVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private UserItemVo assembleUserItemVo(User user) {
        UserItemVo userItemVo = new UserItemVo();
        userItemVo.setId(user.getId());
        userItemVo.setEmail(user.getEmail());
        userItemVo.setUsername(user.getUsername());
        userItemVo.setPhone(user.getPhone());
        userItemVo.setRole(user.getRole());
        userItemVo.setCreateTime(DateTimeUtil.dateToStr(user.getCreateTime()));
        userItemVo.setUpdateTime(DateTimeUtil.dateToStr(user.getUpdateTime()));
        return userItemVo;
    }
}
