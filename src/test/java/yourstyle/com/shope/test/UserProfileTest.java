package yourstyle.com.shope.test;

import org.testng.annotations.Test;
import yourstyle.com.shope.base.BaseTest;
import yourstyle.com.shope.page.LoginPage;
import yourstyle.com.shope.page.UserProfilePage;

public class UserProfileTest extends BaseTest {

    @Test
    public void testInsertInformationUser() {
        try {
            LoginPage.login("Vothithanh", "123456");
            UserProfilePage.UserProfileDetailInsertSuccess("src/main/resources/static/uploads/Avatar/1_anh-cua-shin-1", "0", "VT Thanh",
                    "0349226904");
        } catch (Exception ex) {
            System.out.println("hi" + ex);
        }

    }
}
