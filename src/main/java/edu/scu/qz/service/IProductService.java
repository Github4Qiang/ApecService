package edu.scu.qz.service;

import com.github.pagehelper.PageInfo;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Product;
import edu.scu.qz.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Integer userId, Product product);

    ServerResponse setSaleStatus(Integer role, Integer productId, Integer status);

    ServerResponse forceRecover(Integer role, Integer productId);

    ServerResponse manageProductDetail(Integer productId);

    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse getProductList(Integer status, Integer pageNum, Integer pageSize);

    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<ProductDetailVo> getProductDetail(Integer userId, Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);

    // 卖家方法，返回所有商品列表
    ServerResponse<PageInfo> getShopProductList(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse forceDown(Integer role, Integer productId);
}
