package edu.scu.qz.service.impl;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.service.ICategoryService;
import edu.scu.qz.vo.CategoryVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Rollback
@Transactional("transactionManager")
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml"})

public class CategoryServiceImplTest {

    @Autowired
    private ICategoryService iCategoryService;

    @Test
    public void getCategoryDetail() {
        ServerResponse response = iCategoryService.getCategoryDetail(105004);
        assertTrue(response.isSuccess());
        CategoryVo categoryVo = (CategoryVo) response.getData();
        System.out.println(categoryVo);
    }

}