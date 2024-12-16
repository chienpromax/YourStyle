package yourstyle.com.shope.test;

import org.testng.annotations.Test;
import yourstyle.com.shope.base.BaseTest;
import yourstyle.com.shope.page.LoginPage;
import yourstyle.com.shope.page.VoucherPage;

public class VoucherTest extends BaseTest {

    @Test(priority = 1)
    public void testInsertVoucherSuccess() throws Exception {
        LoginPage.login("guest", "123456");
        Thread.sleep(500);
        VoucherPage.InsertVoucherSuccess("SALE1", "SALE 1/1", "12", "100000",
                "10", "100000", "10", "Giảm giá phần trăm", "2024", "July",
                "12", "2024", "July", "15", "isPublic", "Đã thành công");
    }

    @Test(priority = 2)
    public void testInsertVoucherIsEmpty() throws Exception {
        LoginPage.login("guest", "123456");
        VoucherPage.InsertVoucherInvalid("");
        Thread.sleep(5000);
    }

    @Test(priority = 3)
    public void testFindVoucherByName() throws Exception {
        LoginPage.login("guest", "123456");
        VoucherPage.findVoucherSuccessByName("SALE 12/12");
        Thread.sleep(5000);
    }

    @Test(priority = 4)
    public void testFilterVoucherByStatus() throws Exception {
        LoginPage.login("guest", "123456");
        VoucherPage.filterVoucherByStatus("Công khai");
        Thread.sleep(5000);
    }

    @Test(priority = 5)
    public void testFilterVoucherByType() throws Exception {
        LoginPage.login("guest", "123456");
        VoucherPage.filterVoucherByType("Giảm giá trực tiếp");
        Thread.sleep(5000);
    }
}
