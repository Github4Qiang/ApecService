package edu.scu.qz.service;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.dao.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse getChidrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

    ServerResponse getTop2Category();

    ServerResponse<String> getGeneticList(Integer categoryId);

    ServerResponse getCategoryDetail(Integer categoryId);

    ServerResponse save(Category category);
}
