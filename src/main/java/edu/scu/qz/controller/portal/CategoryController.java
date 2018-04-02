package edu.scu.qz.controller.portal;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/category/")
public class CategoryController {

    @Autowired
    private ICategoryService iCategoryService;

    // 返回前两级品类信息
    @RequestMapping(value = "get_top2_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getTop2Category() {
        return iCategoryService.getTop2Category();
    }

}
