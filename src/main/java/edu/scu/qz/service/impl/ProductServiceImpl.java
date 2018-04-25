package edu.scu.qz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import edu.scu.qz.common.Const;
import edu.scu.qz.common.ResponseCode;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.inherit.ICategoryMapper;
import edu.scu.qz.dao.idao.inherit.IProductMapper;
import edu.scu.qz.dao.idao.inherit.IShopMapper;
import edu.scu.qz.dao.pojo.Category;
import edu.scu.qz.dao.pojo.Product;
import edu.scu.qz.dao.pojo.Shop;
import edu.scu.qz.service.ICategoryService;
import edu.scu.qz.service.IProductService;
import edu.scu.qz.util.DateTimeUtil;
import edu.scu.qz.util.PropertiesUtil;
import edu.scu.qz.vo.ProductDetailVo;
import edu.scu.qz.vo.ProductItemVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    private static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IShopMapper shopMapper;
    @Autowired
    private ICategoryMapper categoryMapper;
    @Autowired
    private IProductMapper productMapper;

    @Override
    public ServerResponse saveOrUpdateProduct(Integer userId, Product product) {
        if (product != null) {
            Shop shop = shopMapper.selectByUserId(userId);
            if (shop == null) {
                return ServerResponse.createByErrorMessage("用户未创建店铺");
            }

            // 第一张子图设置成主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            // product.id 不为空则代表更新操作；为空则代表添加操作
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            } else {
                product.setShopId(shop.getId());
                product.setShopName(shop.getShopName());
                product.setStatus(Const.ProductStatusEnum.ON_SALE.getCode());
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse setSaleStatus(Integer role, Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (status == Const.ProductStatusEnum.FORCE_DOWN.getCode() && Const.Role.ROLE_ADMIN != role) {
            return ServerResponse.createByErrorMessage("您没有强制下架的权限");
        } else if (product.getStatus() == Const.ProductStatusEnum.FORCE_DOWN.getCode() && role != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("商品已经被强制下架，请联系管理员恢复商品状态");
        } else if (product.getStatus() == Const.ProductStatusEnum.DELETE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已被删除");
        }

        Product updateProduct = new Product();
        updateProduct.setId(productId);
        updateProduct.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse forceDown(Integer role, Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (Const.Role.ROLE_ADMIN != role) {
            return ServerResponse.createByErrorMessage("您没有强制下架的权限");
        } else if (product.getStatus() == Const.ProductStatusEnum.FORCE_DOWN.getCode()) {
            return ServerResponse.createByErrorMessage("商品已经被强制下架，请勿重复操作");
        } else if (product.getStatus() == Const.ProductStatusEnum.DELETE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已被删除，不用再去管它");
        }

        Product updateProduct = new Product();
        updateProduct.setId(productId);
        updateProduct.setStatus(Const.ProductStatusEnum.FORCE_DOWN.getCode());
        int rowCount = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse forceRecover(Integer role, Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品不存在：" + productId);
        } else if (Const.Role.ROLE_ADMIN != role) {
            return ServerResponse.createByErrorMessage("您没有恢复强制下架的权限");
        } else if (product.getStatus() != Const.ProductStatusEnum.FORCE_DOWN.getCode()) {
            return ServerResponse.createByErrorMessage("商品没有被强制下架，不需要恢复");
        }

        Product updateProduct = new Product();
        updateProduct.setId(productId);
        updateProduct.setStatus(Const.ProductStatusEnum.ON_SALE.getCode());
        int rowCount = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    // 后台方法，返回商品列表，已下架的商品也会返回
    @Override
    public ServerResponse getProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductItemVo> productItemVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductItemVo productItemVo = assembleProductItemVo(productItem);
            productItemVoList.add(productItemVo);
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(productList);
        // 将 PageInfo 中数据换成 ProductItemVo-List
        pageResult.setList(productItemVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    // 后台方法，返回商品列表，包括已下架的商品
    @Override
    public ServerResponse getProductList(Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = null;
        if (status < 0) {
            productList = productMapper.selectList();
        } else if (status == Const.ProductStatusEnum.DELETE.getCode()) {
            return ServerResponse.createByErrorMessage("删除的商品没什么用了，不要管他啦~");
        } else {
            productList = productMapper.selectListByStatus(status);
        }
        List<ProductItemVo> productItemVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductItemVo productItemVo = assembleProductItemVo(productItem);
            productItemVoList.add(productItemVo);
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(productList);
        // 将 PageInfo 中数据换成 ProductItemVo-List
        pageResult.setList(productItemVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    // 后台方法，根据产品名称或ID返回商品，已下架的商品也会返回
    @Override
    public ServerResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductItemVo> productItemVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductItemVo productItemVo = assembleProductItemVo(productItem);
            productItemVoList.add(productItemVo);
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(productList);
        // 将 PageInfo 中数据换成 ProductItemVo-List
        pageResult.setList(productItemVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    // 买家用的方法，只能查看在售商品
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品下架或删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    // 卖家、管理员用的方法，可以查看在售、下架商品
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer userId, Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品下架或删除");
        }
        // 传入 userId 的是卖家；不传的是管理员
        if (userId != null) {
            Shop shop = shopMapper.selectByUserId(userId);
            if (shop == null) {
                return ServerResponse.createByErrorMessage("用户未开通店铺");
            } else if (product.getShopId() != shop.getId()) {
                return ServerResponse.createByErrorMessage("这不是您店铺中的商品：" + product.getId());
            }
        }

        if (product.getStatus() == Const.ProductStatusEnum.ON_SALE.getCode() || product.getStatus() == Const.ProductStatusEnum.TAKE_DOWN.getCode()) {
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
        }
        return ServerResponse.createByErrorMessage("产品下架或删除");
    }

    // 前台方法，根据关键字或产品类别返回在售商品，已下架的商品不会返回
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            // 当该分类不存在，并且也没有关键字时，返回空的结果集
            if (category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductItemVo> productItemVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productItemVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            // 当该分类存在时，得到所有该分类的子分类
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        // 处理排序
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                // PageHelper 排序规则：[属性名 asc/desc]
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(keyword) ? null : keyword,
                categoryIdList.isEmpty() ? null : categoryIdList);
        List<ProductItemVo> productItemVoList = Lists.newArrayList();
        for (Product product : productList) {
            // 只返回在售商品；已下架的商品跳过
            if (product.getStatus() == Const.ProductStatusEnum.ON_SALE.getCode()) {
                ProductItemVo productItemVo = assembleProductItemVo(product);
                productItemVoList.add(productItemVo);
            }
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productItemVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    // 卖家方法，返回所有商品列表
    @Override
    public ServerResponse<PageInfo> getShopProductList(Integer userId, Integer pageNum, Integer pageSize) {
        Shop shop = shopMapper.selectByUserId(userId);
        if (shop == null) {
            return ServerResponse.createByErrorMessage("用户未开通店铺");
        } else if (shop.getProducerId() != userId) {
            return ServerResponse.createByErrorMessage("这可不是您的店铺，别乱动哦~");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByShopId(shop.getId());
        List<ProductItemVo> productItemVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            if (productItem.getStatus() != Const.ProductStatusEnum.DELETE.getCode()) {
                ProductItemVo productItemVo = assembleProductItemVo(productItem);
                productItemVoList.add(productItemVo);
            }
        }
        // 根据 Product-List 计算 PageInfo 中参数值
        PageInfo pageResult = new PageInfo(productItemVoList);
        // 将 PageInfo 中数据换成 ProductItemVo-List
        pageResult.setList(productItemVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setShopId(product.getShopId());
        productDetailVo.setShopName(product.getShopName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryIdLv3(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStatusDesc(Const.ProductStatusEnum.codeOf(product.getStatus()).getValue());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.apec.com/"));
        // 三级品类详情
        Category categoryLv3 = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (categoryLv3 != null) {
            productDetailVo.setCategoryIdLv2(categoryLv3.getParentId());
            // 二级品类详情
            Category categoryLv2 = categoryMapper.selectByPrimaryKey(categoryLv3.getParentId());
            if (categoryLv2 != null) {
                productDetailVo.setCategoryIdLv1(categoryLv2.getParentId());
            } else {
                logger.warn("二级品类不存在");
            }
        } else {
            logger.warn("三级品类不存在");
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    private ProductItemVo assembleProductItemVo(Product product) {
        ProductItemVo productItemVo = new ProductItemVo();
        productItemVo.setId(product.getId());
        productItemVo.setShopId(product.getShopId());
        productItemVo.setShopName(product.getShopName());
        productItemVo.setSubtitle(product.getSubtitle());
        productItemVo.setStock(product.getStock());
        productItemVo.setPrice(product.getPrice());
        productItemVo.setStatusDesc(Const.ProductStatusEnum.codeOf(product.getStatus()).getValue());
        productItemVo.setMainImage(product.getMainImage());
        productItemVo.setName(product.getName());
        productItemVo.setStatus(product.getStatus());
        productItemVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://image.apec.com/"));

        productItemVo.setCategory(iCategoryService.getGeneticList(product.getCategoryId()).getData());

        return productItemVo;
    }
}
