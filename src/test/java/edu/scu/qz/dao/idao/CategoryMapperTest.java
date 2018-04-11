package edu.scu.qz.dao.idao;

import com.google.common.collect.Lists;
import edu.scu.qz.dao.idao.inherit.ICategoryMapper;
import edu.scu.qz.dao.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品类别获取工具类
 * · 从“惠农网”爬虫抓取商品类别，并存入数据库中
 * · 由于 ChromeDriver 的缘故，需要使用 gson-v-2.8.2
 * · 使用前修改 pom.xml；使用后也要改回去！
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml"})
public class CategoryMapperTest {
    @Autowired
    private ICategoryMapper categoryMapper;
    private ArrayList<Integer> parentStack = Lists.newArrayList();

    private Category assembleCategory(String name, Integer parentId) {
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        return category;
    }

    @Test
    public void generateDefaultCategories() {
        // 设定连接 Chrome 浏览器驱动程序所在的磁盘位置，并添加为系统属性值
        String chromeDriverLocation = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverLocation);
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.cnhnb.com/supply/index.htm");

        // 分别找到各级分类的区域，并赋值给局部变量
        WebElement lv1ElementsArea = driver.findElement(By.xpath("//*[@id=\"category_ul\"]"));
        WebElement lv2ElementsArea = driver.findElement(By.xpath("//*[@id=\"cate2_div\"]"));
        WebElement lv3ElementsArea = driver.findElement(By.xpath("//*[@id=\"cate3_div\"]"));

        parentStack.add(0);

        // 获取一级分类列表
        List<WebElement> lv1ElementList = lv1ElementsArea.findElements(By.tagName("li"));
        for (int i = 1; i < lv1ElementList.size() - 1; i++) {
            // 点击一级分类
            WebElement lv1Element = lv1ElementList.get(i);
            lv1Element.click();

            // 记录下 Lv-1 分类
            String lv1CategoryName = lv1Element.getText().trim();
            Category lv1Category = assembleCategory(lv1CategoryName, parentStack.get(parentStack.size() - 1));

            // 存到数据库中
            categoryMapper.insert(lv1Category);
            // 返回 Lv-1 分类 ID，并且入父类栈
            Integer lv1CategoryId = categoryMapper.selectByCategoryName(lv1Category.getName()).getId();
            parentStack.add(lv1CategoryId);

            // 先点击一下第一个二级分类，让控制权交给三级分类
            WebElement firstOfLv2Element = lv2ElementsArea.findElement(By.xpath("//*[@id=\"cate2_div\"]/div[1]/a[1]"));
            firstOfLv2Element.click();

            // 从三级分类区域中，获取二级分类列表
            List<WebElement> lv2ElementList = lv3ElementsArea.findElement(By.className("list-t")).findElements(By.tagName("a"));
            for (int j = 0; j < lv2ElementList.size(); j++) {
                // 点击二级分类
                lv2ElementList.get(j).click();

                // 记录下 Lv-2 分类
                String lv2CategoryName = lv2ElementList.get(j).getText().trim();
                Category lv2Category = assembleCategory(lv2CategoryName, parentStack.get(parentStack.size() - 1));

                // 存到数据库中
                categoryMapper.insert(lv2Category);
                // 返回 Lv-1 分类 ID，并且入父类栈
                Integer lv2CategoryId = categoryMapper.selectByCategoryName(lv2Category.getName()).getId();
                parentStack.add(lv2CategoryId);

                // 获取三级分类列表
                List<WebElement> lv3ElementList = lv3ElementsArea.findElements(By.className("list-c")).get(j)
                        .findElements(By.tagName("a"));
                for (WebElement lv3Element : lv3ElementList) {
                    // 记录下 Lv-2 分类
                    String lv3CategoryName = lv3Element.getText().trim();
                    Category lv3Category = assembleCategory(lv3CategoryName, parentStack.get(parentStack.size() - 1));
                    // 存到数据库中
                    categoryMapper.insert(lv3Category);
                }
                // Lv-2 分类出父类栈
                parentStack.remove(parentStack.size() - 1);
            }
            // Lv-1 分类出父类栈
            parentStack.remove(parentStack.size() - 1);
        }

    }
}