package edu.scu.qz.controller.cybershop;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Product;
import edu.scu.qz.dao.pojo.User;
import edu.scu.qz.service.IFileService;
import edu.scu.qz.service.IProductService;
import edu.scu.qz.service.IUserService;
import edu.scu.qz.util.PropertiesUtil;
import edu.scu.qz.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 *
 * todo: 1. 刚添加的商品状态为“待审核”
 * todo: 2. 下架后的商品再上架需要审核，所以：下架 -> 待审核 -> 上架
 * todo: 3. 审核不通过：UN_PASS
 * todo: 4. 只有“下架”“审核未通过”“强制下架”的商品可以被删除
 */
@Controller
@RequestMapping("/producer/product/")
public class ProductCyberController {

    @Autowired
    private IFileService iFileService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> save(HttpSession session, Product product) {
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
        return iProductService.saveOrUpdateProduct(user.getId(), product);
    }

    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(HttpSession session, Integer productId) {
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
        return iProductService.getProductDetail(user.getId(), productId);
    }

    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
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
        return iProductService.getShopProductList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
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
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccess(fileMap);
    }


    // Simditor 有固定的文件上传返回格式
    //    {
    //        "success": true/false,
    //            "msg": "error message", # optional
    //        "file_path": "[real file path]"
    //    }
    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map<String, Object> richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("success", false);

        // 判断用户已登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("msg", ResponseCode.NEED_LOGIN.getDesc());
            return resultMap;
        } else if (user.getRole().intValue() == Const.Role.ROLE_CUSTOMER) {
            // 尚未提交申请信息
            resultMap.put("msg", ResponseCode.NEED_SUBMIT_SHOP_INFO.getDesc());
            return resultMap;
        } else if (user.getRole().intValue() == Const.Role.ROLE_APPLICANT) {
            // 已提交申请，等待管理员审核
            resultMap.put("msg", ResponseCode.WAIT_ADMIN_VERIFY.getDesc());
            return resultMap;
        } else if (user.getRole().intValue() == Const.Role.ROLE_CANDIDATE) {
            // 管理员审核成功，等待用户激活店铺
            resultMap.put("msg", ResponseCode.NEED_ACTIVATE.getDesc());
            return resultMap;
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("msg", "文件上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }

    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
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
        return iProductService.setSaleStatus(user.getRole(), productId, status);
    }

    @RequestMapping(value = "delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer productId) {
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
        return iProductService.setSaleStatus(user.getRole(), productId, Const.ProductStatusEnum.DELETE.getCode());
    }
}
