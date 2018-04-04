package portal.customer.user;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertEquals;

/**
 * 注册页面测试
 */
public class RegisterTest {

    private WebDriver driver;
    private String baseUrl = "http://www.apec.com/user-register.html";

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @Test
    public void testTitle() {
        driver.get(baseUrl);
        String contentTitle = driver.findElement(By.className("user-title")).getText();
        assertEquals("注册框标题", "用户注册", contentTitle);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}
