package yourstyle.com.shope.test;

import org.testng.annotations.Test;
import yourstyle.com.shope.base.BaseTest;
import yourstyle.com.shope.page.LoginPage;
import yourstyle.com.shope.page.ProductPage;

public class ProductTest extends BaseTest {

    @Test
    public void testInsertProductSuccess() {
        LoginPage.login("guest", "123456");
        ProductPage.insertProductSuccess(
                "Chân váy jean dài KABICO cạp điều chỉnh độ rộng bụng phong cách, váy jean dáng dài cạp cao thanh lịch A14",
                "137", 6,
                "Chân váy jean dài KABICO cạp điều chỉnh độ rộng bụng phong cách, váy jean dáng dài cạp cao thanh lịch A14",
                "- Chất lượng bền đẹp, luôn đặt uy tín lên hàng đầu", "true");
    }

}
