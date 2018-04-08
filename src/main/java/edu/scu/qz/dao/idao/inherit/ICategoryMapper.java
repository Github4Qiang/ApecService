package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.CategoryMapper;
import edu.scu.qz.dao.pojo.Category;

import java.util.List;

public interface ICategoryMapper extends CategoryMapper {

    List<Category> selectCategoryChildrenByParentId(Integer parentId);

}
