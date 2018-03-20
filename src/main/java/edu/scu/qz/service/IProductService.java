package edu.scu.qz.service;

import com.github.pagehelper.PageInfo;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Product;
import edu.scu.qz.vo.ProductDetailVo;
import org.springframework.stereotype.Service;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse manageProductDetail(Integer productId);

    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);
}
