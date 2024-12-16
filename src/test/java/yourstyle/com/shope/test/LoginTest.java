package yourstyle.com.shope.test;

import org.testng.annotations.Test;

import yourstyle.com.shope.base.BaseSetup;
import yourstyle.com.shope.base.BaseTest;
import yourstyle.com.shope.page.LoginPage;

public class LoginTest extends BaseTest {
    static LoginPage loginPage = new LoginPage(BaseSetup.driver);

    // test login success
    @Test
    public void loginByAdmin() {
        loginPage.login("guest", "123456");
    }

    // test login invalid user
    @Test
    public  void loginInvalidUserName() {
        loginPage.loginInvalid("Vothithan", "123456");
    }

    // test login invalid pass
    @Test
    public void loginInvalidPassWord() {
        loginPage.loginInvalid("Vothithanh", "123479");
    }

    // test login empty username
    @Test
    public  void loginEmptyUserName() {
        loginPage.loginInvalidEmptyUserName("", "123456");
    }

    // test login empty password
    @Test
    public void loginEmptyPassWord() {
        loginPage.loginInvalidEmptyPassWord("Vothithanh", "");
    }
}
