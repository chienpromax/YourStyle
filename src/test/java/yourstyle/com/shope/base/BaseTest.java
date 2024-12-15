package yourstyle.com.shope.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import yourstyle.com.shope.page.LoginPage;


public class BaseTest {
    protected LoginPage loginPage;

    @BeforeMethod
    public void setUp() {
        BaseSetup.setupDriver();
        loginPage = new LoginPage(BaseSetup.driver);
    }

    @AfterMethod
    public void tearDown() {
        BaseSetup.driver.quit();
    }

}
