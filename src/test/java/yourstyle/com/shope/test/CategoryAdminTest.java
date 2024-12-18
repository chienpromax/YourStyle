package yourstyle.com.shope.test;

import org.testng.annotations.Test;

import yourstyle.com.shope.base.BaseTest;
import yourstyle.com.shope.page.CategoryPage;
import yourstyle.com.shope.page.LoginPage;

public class CategoryAdminTest extends BaseTest {

    @Test(priority = 1)
    public void testDeleteCategory() {
        LoginPage.login("admin", "admin");
        CategoryPage.deleteCategory(4, "Áo len đônO3");
    }

    @Test(priority = 2)
    public void testAddAccountSuccess() throws Exception {
        LoginPage.login("admin", "admin");
        CategoryPage.addCategory("Áo len đônO4", "Thời trang nữ",
                "src\\main\\resources\\static\\uploads\\reviewImages\\maomao.jpg");
    }

    @Test(priority = 3)
    public void testAddCategoryWithEmptyName() throws Exception {
        LoginPage.login("admin", "admin");
        CategoryPage.addCategoryEmpty("", "Thời trang nữ", "src\\main\\resources\\static\\uploads\\aolen.png");

    }

    @Test(priority = 4)
    public void testAddCategoryEmptyImage() throws Exception{
        LoginPage.login("admin", "admin");
        // Thêm danh mục mà không chọn ảnh
        CategoryPage.addCategoryEmpty("Áo len đôngSS", "Thời trang nữ", "");

    }

    @Test(priority = 5)
    public void testEditCategory() throws Exception {
        LoginPage.login("admin", "admin");
        CategoryPage.editCategory(53, "Danh mục sửaa", "Thời trang nữ",
                "src\\main\\resources\\static\\uploads\\reviewImages\\maomao.jpg");
        Thread.sleep(5000);
    }

}
