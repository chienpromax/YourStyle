package yourstyle.com.shope.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import yourstyle.com.shope.pages.AccountAdminPage;
import yourstyle.com.shope.pages.LoginPage;

public class AccountAdminTest {
    private WebDriver driver;
    private AccountAdminPage accountAdminPage;
    private LoginPage loginPage;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        accountAdminPage = new AccountAdminPage(driver);
        loginPage = new LoginPage(driver);
    }

    @BeforeMethod
    public void loginAndNavigateToAddPage() {
        driver.get("http://localhost:8080/yourstyle/accounts/login");
        loginPage.login("admin", "admin");
        accountAdminPage.openAddAccountPage();
        accountAdminPage.openSearchAccountPage();
    }

    @Test(priority = 1)
    public void testAddAccountSuccess() {
        accountAdminPage.fillAccountForm("thanhvo81", "thanhvo81", "thanhvo81@example.com", "ROLE_USER");
        accountAdminPage.clickCreateButton();
        accountAdminPage.verifySuccessToast();
    }

    @Test(priority = 2)
    public void testAddAccountWithEmptyUsername() {
        accountAdminPage.fillAccountForm("", "testpassword", "testuser@example.com", "ROLE_USER");
        accountAdminPage.clickCreateButton();
        String notification = accountAdminPage.getNotificationMessage();
        Assert.assertTrue(notification.contains("Vui lòng nhập tên tài khoản!"));
    }

    @Test(priority = 3)
    public void testAddAccountWithEmptyPassword() {
        accountAdminPage.fillAccountForm("Linh", "", "Linh@gmail.com", "ROLE_EMPLOYEE");
        accountAdminPage.clickCreateButton();
        String notification = accountAdminPage.getNotificationMessage();
        Assert.assertTrue(notification.contains("Vui lòng nhập mật khẩu!"));
    }

    @Test(priority = 4)
    public void testAddAccountWithEmptyEmail() {
        accountAdminPage.fillAccountForm("Linh", "Linh", "", "ROLE_EMPLOYEE");
        accountAdminPage.clickCreateButton();

        String notification = accountAdminPage.getNotificationMessage();
        Assert.assertTrue(notification.contains("Vui lòng nhập địa chỉ email!"),
                "Không có thông báo lỗi khi tên người dùng bỏ trống.");
    }

    @Test(priority = 5)
    public void testAddAccountWithInvalidEmail() {
        accountAdminPage.fillAccountForm("Linh", "Linh", "Linh.com", "ROLE_EMPLOYEE");
        accountAdminPage.clickCreateButton();

        String notification = accountAdminPage.getNotificationMessage();
        Assert.assertTrue(notification.contains("Địa chỉ email không hợp lệ!"),
                "Không có thông báo lỗi khi tên người dùng bỏ trống.");
    }

    @Test(priority = 6)
    public void testEditAccount() throws Exception {
        accountAdminPage.openEditAccountPage(26);
        accountAdminPage.fillAccountForm("newusername", null, "newemail@example.com", "ROLE_ADMIN");
        accountAdminPage.clickUpdateButton();
        Thread.sleep(5000);
    }

    @Test(priority = 7)
    public void testEditAccountWithEmptyUsername() {
        accountAdminPage.openEditAccountPage(26); // ID tài khoản cần chỉnh sửa
        accountAdminPage.fillAccountForm("", null, "newemail@example.com", "ROLE_ADMIN");
        // Cập nhật tài khoản với tên người dùng bỏ trống
        accountAdminPage.clickUpdateButton();
        // Kiểm tra thông báo lỗi
        String notification = accountAdminPage.getNotificationMessage();
        Assert.assertTrue(notification.contains("Vui lòng nhập tên tài khoản!"),
                "Không có thông báo lỗi khi tên người dùng bỏ trống.");
    }

    @Test(priority = 9)
    public void testEditAccountWithEmptyEmail() {
        accountAdminPage.openEditAccountPage(63);
        accountAdminPage.fillAccountForm("Linh", null, "", "ROLE_USER");
        accountAdminPage.clickUpdateButton();

        String notification ="//small[text()='[Vui lòng nhập địa chỉ email!]']";
        WebElement actualResult = driver.findElement(By.xpath(notification));
        String actualResultIsEmptyEmail = actualResult.getText();
        Assert.assertEquals(actualResultIsEmptyEmail,"[Vui lòng nhập địa chỉ email!]","Ket qua mong doi va ket qua thuc te khong giong nhau");
    
    }
    @Test(priority = 10)
    public void testEditAccountWithInvalidEmail() {
        accountAdminPage.openEditAccountPage(63);
        accountAdminPage.fillAccountForm("Linh", null, "linhgmail", "ROLE_USER");
        accountAdminPage.clickUpdateButton();

        String notification ="//small[text()='[Địa chỉ email không hợp lệ!]']";;
        WebElement actualResult = driver.findElement(By.xpath(notification));
        String actualResultIsEmptyEmail = actualResult.getText();
        Assert.assertEquals(actualResultIsEmptyEmail,"[Địa chỉ email không hợp lệ!]","Ket qua mong doi va ket qua thuc te khong giong nhau");
    }

    @Test(priority = 10)
    public void testSearchAccountSuccess() throws Exception {
        // Tìm kiếm tài khoản với username tồn tại
        String username = "linhuser";
        accountAdminPage.searchAccount(username);

        // Kiểm tra kết quả tìm kiếm có hiển thị tài khoản đúng
        Assert.assertTrue(accountAdminPage.isAccountDisplayed(username),
                "Tài khoản không hiển thị trong kết quả tìm kiếm.");
        Thread.sleep(5000);
    }

    @Test(priority = 9)
    public void testSearchAccountNotFound() throws Exception {
        // Tìm kiếm tài khoản không tồn tại
        String nonExistentUsername = "unknownUser";
        accountAdminPage.searchAccount(nonExistentUsername);

        // Kiểm tra kết quả không hiển thị tài khoản nào
        Assert.assertFalse(accountAdminPage.isAccountDisplayed(nonExistentUsername),
                "Kết quả tìm kiếm hiển thị tài khoản không tồn tại.");
        Thread.sleep(5000);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
