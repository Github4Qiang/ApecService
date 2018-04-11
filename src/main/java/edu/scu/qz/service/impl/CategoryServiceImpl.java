package edu.scu.qz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.idao.inherit.ICategoryMapper;
import edu.scu.qz.dao.pojo.Category;
import edu.scu.qz.service.ICategoryService;
import edu.scu.qz.util.PropertiesUtil;
import edu.scu.qz.vo.CategoryVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ICategoryMapper categoryMapper;

    // todo: 添加品类时要上传图片
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); // 该品类是可用的

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryName == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类名称参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名称失败");
    }

    @Override
    public ServerResponse getChidrenParallelCategory(Integer categoryId) {
        List<CategoryVo> categoryVoList = Lists.newArrayList();
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类：" + categoryId);
        }
        for (Category category : categoryList) {
            CategoryVo categoryVo = assembleCategoryVo(category);
            categoryVoList.add(categoryVo);
        }
        return ServerResponse.createBySuccess(categoryVoList);
    }


    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategories(categorySet, categoryId);

        List<Integer> categoryList = Lists.newArrayList();
        for (Category categoryItem : categorySet) {
            categoryList.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    // 获取前两级品类数据
    @Override
    public ServerResponse getTop2Category() {
        // 获取第一级品类数据
        List<CategoryVo> categoryVoList = Lists.newArrayList();
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(0);
        for (Category categoryItem : categoryList) {
            // 获取每个一级品类的子品类
            List<CategoryVo> categoryLv1VoList = Lists.newArrayList();
            List<Category> categoryLv1List = categoryMapper.selectCategoryChildrenByParentId(categoryItem.getId());
            for (Category categoryLv1Item : categoryLv1List) {
                CategoryVo categoryLv1Vo = assembleCategoryVo(categoryLv1Item);
                categoryLv1VoList.add(categoryLv1Vo);
            }

            // 把第一级品类转换成 VO
            CategoryVo categoryVo = assembleCategoryVo(categoryItem);
            categoryVo.setChildren(categoryLv1VoList);
            categoryVoList.add(categoryVo);
        }
        return ServerResponse.createBySuccess(categoryVoList);
    }

    private CategoryVo assembleCategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        categoryVo.setId(category.getId());
        categoryVo.setParentId(category.getParentId());
        categoryVo.setName(category.getName());
        categoryVo.setImage(category.getImage());
        categoryVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        categoryVo.setStatus(category.getStatus());
        categoryVo.setSortOrder(category.getSortOrder());
        categoryVo.setCreateTime(category.getCreateTime());
        categoryVo.setUpdateTime(category.getUpdateTime());
        return categoryVo;
    }

    private Set<Category> findChildCategories(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategories(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}

