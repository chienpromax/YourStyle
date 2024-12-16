package yourstyle.com.shope.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import yourstyle.com.shope.pages.CategoryPage;
import yourstyle.com.shope.pages.LoginPage;

public class CategoryAdminTest {
    private WebDriver driver;
    private CategoryPage categoryPage;
    private LoginPage loginPage;
    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        categoryPage = new CategoryPage(driver);
        loginPage = new LoginPage(driver);
    }

    // lafm ri ko toi gian, lop mô cũng phải có beforre method , lặp code nhìu quá á
    // hic, xíu a gửi bài a cho tham khảo
    @BeforeMethod
    public void loginAndNavigate() {
        driver.get("http://localhost:8080/yourstyle/accounts/login");
        loginPage.login("admin", "admin");
        categoryPage.openAddCategoryPage();
    }

    @Test(priority = 1)
    public void testDeleteCategory() {
        CategoryPage.deleteCategory(3, "Thời trang nữ");
    }

    @Test(priority = 2)
    public void testAddAccountSuccess() throws Exception {
        CategoryPage.addCategory("Áo len đôngggggg", "Thời trang nữ", "src\\main\\resources\\static\\uploads\\aolen.png");
        Thread.sleep(5000);
    }
    @Test(priority = 3)
public void testAddCategoryWithEmptyName() {
        CategoryPage.addCategory("","Thời trang nữ", "src\\main\\resources\\static\\uploads\\aolen.png");
  
}


   @Test(priority = 3)
public void testAddAccountEmptyImage() {
    // Thêm danh mục mà không chọn ảnh
    categoryPage.addCategory("Áo len đôngSS", "Thời trang nữ", ""); 
    
}
    @Test(priority = 4)
    public void testEditCategory() throws Exception {
        CategoryPage.editCategory(53, "Danh mục sửaaa", "Thời trang nữ",
                "D:\\DATN\\TestCase\\YourStyle\\src\\main\\resources\\static\\uploads\\reviewImages\\maomao.jpg");
                Thread.sleep(5000);
    }
 

  
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
