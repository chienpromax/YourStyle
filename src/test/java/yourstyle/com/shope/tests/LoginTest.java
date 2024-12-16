package yourstyle.com.shope.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import yourstyle.com.shope.pages.LoginPage;

public class LoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {
        // Cài đặt WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/yourstyle/accounts/login");

        // Khởi tạo LoginPage
        loginPage = new LoginPage(driver);
    }

    @Test
    public void testLogin() {
        // Sử dụng LoginPage để thực hiện kiểm thử
        loginPage.enterUsername("admin");
        loginPage.enterPassword("admin");
        loginPage.clickLoginButton();

        // Kiểm tra xem đã chuyển đến URL mong đợi hay chưa
        String expectedUrl = "http://localhost:8080/admin/home";
        Assert.assertTrue(loginPage.isOnHomePage(expectedUrl), "Đăng nhập thất bại!");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
