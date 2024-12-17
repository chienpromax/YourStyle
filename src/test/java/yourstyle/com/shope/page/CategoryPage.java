package yourstyle.com.shope.page;

import java.io.File;
import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import yourstyle.com.shope.base.BaseSetup;
import yourstyle.com.shope.untils.InputValidation;
import yourstyle.com.shope.untils.ScrollTo;
import yourstyle.com.shope.untils.WaitFor;

public class CategoryPage {

    public static  void openAddCategoryPage() {
        String menuCategory = "//span[text()='Quản lý danh mục']";
        String clickCategory = "//span[text()='Danh mục']";
        WebElement clickMenu = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(menuCategory));
        clickMenu.click();
        WebElement clickMenuCategory = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(clickCategory));
        clickMenuCategory.click();
    }

    // xoas
    public static void deleteCategory(int page, String valueDelete) {
        Actions actions = new Actions(BaseSetup.driver);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) BaseSetup.driver;
        String clickPage = "//li[@class='page-item']/a[text()='" + page + "']";
        String btnDelete = "//tbody/tr/td[text()='" + valueDelete + "']/../td[@class='text-nowrap']/button";
        String confirmDelete = "//div[@class='modal fade show']/div/div/div[@class='modal-footer']/a";
        String resultDelete = "//span[text()='Xóa danh mục thành công']";
        String checkDeleteSucces = "//tbody/tr/td[(@class='text-truncate category-name') and (text()='" + valueDelete
                + "')]";
                //chuyen den categorycategory
        openAddCategoryPage();
        // Cuộn xuống 3 lần trước khi click vào trang
        for (int i = 0; i < 3; i++) {
            jsExecutor.executeScript("window.scrollBy(0, document.body.scrollHeight)");
            try {
                Thread.sleep(1000); // Chờ để nội dung tải
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        WebElement clickPagee = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(clickPage));
        clickPagee.click();
        // actions.moveToElement(clickPagee).click().build().perform();

        WebElement clickDelete = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(btnDelete));
        clickDelete.click();
        WebElement clickConfirmDelete = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(confirmDelete));
        clickConfirmDelete.click();
        WebElement actualDelete = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(resultDelete));
        String actualDeleteSucess = actualDelete.getText();
        Assert.assertEquals(actualDeleteSucess, "Xóa danh mục thành công",
                "Kết quả mong đợi và kết quả thực tết không giống nhau");
        // try {
        //     BaseSetup.driver.findElement(By.xpath(checkDeleteSucces));
        //     System.out.println("Danh mục vẫn còn tồn tại!");
        // } catch (NoSuchElementException ex) {
        //     System.out.println("Danh mục đã được xóa");
        // }

    }

    public static void selectCategory(String value) {
        By drpCategory = By.xpath("//select[@id='categoryParent']");
        By selectCate = By.xpath("//select[@id='categoryParent']/option[text()='" + value + "']");
        WebElement clickCate = WaitFor.waitElementVisible(BaseSetup.driver, drpCategory);
        ScrollTo.element(clickCate);
        clickCate.click();
        WebElement choiceCate = WaitFor.waitElementVisible(BaseSetup.driver, selectCate);
        choiceCate.click();
    }

    public static void addCategory(String name, String parentCategory, String imagePath) throws Exception {
        
        openAddCategoryPage();
        InputValidation.clearInput(BaseSetup.driver, name, By.id("name"));
        Thread.sleep(5000);
        selectCategory(parentCategory);
        // Select parentCategorySelect = new
        // Select(BaseSetup.driver.findElement(By.id("categoryParent")));
        // parentCategorySelect.selectByVisibleText(parentCategory);
        // Thread.sleep(10000);
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("File không tồn tại: " + imagePath);
            }
            WebElement imageField = BaseSetup.driver.findElement(By.id("imageInput"));
            imageField.sendKeys(imageFile.getAbsolutePath());
        }
        Thread.sleep(5000);
        WaitFor.waitElementVisible(BaseSetup.driver, By.xpath("//button[contains(text(), 'Thêm')]"));

        WebElement addButton = WaitFor.waitElementClick(BaseSetup.driver,
                By.xpath("//button[contains(text(), 'Thêm')]"));
                ScrollTo.element(addButton);
        addButton.click();
     
        // // Chờ nút "Thêm" có thể nhấp
        // WebDriverWait wait = new WebDriverWait(BaseSetup.driver,
        // Duration.ofSeconds(10));
        // WebElement addButton = wait
        // .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),
        // 'Thêm')]")));
        // Cuộn vào vị trí nút (nếu cần)
        // ((JavascriptExecutor)
        // BaseSetup.driver).executeScript("arguments[0].scrollIntoView(true);",
        // addButton);
        // // Nhấp nút bằng JavaScript
        // ((JavascriptExecutor)
        // BaseSetup.driver).executeScript("arguments[0].click();", addButton);

        // Kiểm tra trường hợp để trống tên danh mục
        // if (name == null || name.trim().isEmpty()) {
        // WebElement nameFieldError = WaitFor.waitElementVisible(BaseSetup.driver,
        // By.id("name"));
        // String validationMessage = nameFieldError.getAttribute("validationMessage");
        // Assert.assertEquals(validationMessage, "Please fill out this field.",
        // "Không có thông báo lỗi khi để trống tên danh mục.");
        // return; // Kết thúc nếu có lỗi
        // }
        // // Kiểm tra thông báo lỗi nếu không chọn ảnh
        // try {
        // WebElement errorMessage = WaitFor.waitElementVisible(BaseSetup.driver,
        // By.id("error-message"));
        // String errorText = errorMessage.getText();
        // Assert.assertTrue(errorText.contains("Vui lòng chọn ảnh cho danh mục."),
        // "Không có thông báo lỗi phù hợp khi không chọn ảnh.");
        // } catch (TimeoutException e) {
        WebElement successToast = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(
                "//div[@class='toast success']/div[@class='content']/span[text()='Thêm mới danh mục thành công']"));
        String successMessage = successToast.getText();
        Assert.assertEquals(successMessage, "Thêm mới danh mục thành công", "Thông báo không khớp.");
        // }
    }

    public static void editCategory(int categoryId, String name, String parentCategory, String imagePath) {
        BaseSetup.driver.get("http://localhost:8080/admin/categories/edit/" + categoryId);

        WebElement categoryNameField = WaitFor.waitElementVisible(BaseSetup.driver, By.id("name"));
        categoryNameField.clear();
        categoryNameField.sendKeys(name);

        Select parentCategorySelect = new Select(BaseSetup.driver.findElement(By.id("categoryParent")));
        parentCategorySelect.selectByVisibleText(parentCategory);

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("File không tồn tại: " + imagePath);
            }
            WebElement imageField = BaseSetup.driver.findElement(By.id("imageInput"));
            imageField.sendKeys(imageFile.getAbsolutePath());
        }

        WebDriverWait wait = new WebDriverWait(BaseSetup.driver, Duration.ofSeconds(20));
        WebElement updateButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Cập nhật')]")));

        ((JavascriptExecutor) BaseSetup.driver).executeScript("arguments[0].scrollIntoView(true);", updateButton);
        ((JavascriptExecutor) BaseSetup.driver).executeScript("arguments[0].click();", updateButton);

        WebElement successToast = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                        "//div[@class='toast success']/div[@class='content']/span[text()='Cập nhật thành công']")));
        String successMessage = successToast.getText();
        Assert.assertEquals(successMessage, "Cập nhật thành công", "Thông báo không khớp.");
    }

    public static void addCategoryEmpty(String name, String parentCategory, String imagePath) throws Exception {
        
        openAddCategoryPage();
        InputValidation.clearInput(BaseSetup.driver, name, By.id("name"));
        Thread.sleep(5000);
        selectCategory(parentCategory);
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("File không tồn tại: " + imagePath);
            }
            WebElement imageField = BaseSetup.driver.findElement(By.id("imageInput"));
            imageField.sendKeys(imageFile.getAbsolutePath());
        }
        Thread.sleep(5000);
        WaitFor.waitElementVisible(BaseSetup.driver, By.xpath("//button[contains(text(), 'Thêm')]"));

        WebElement addButton = WaitFor.waitElementClick(BaseSetup.driver,
                By.xpath("//button[contains(text(), 'Thêm')]"));
                ScrollTo.element(addButton);
        addButton.click();
       
        // Kiểm tra trường hợp để trống tên danh mục
        if (name == null || name.trim().isEmpty()) {
        WebElement nameFieldError = WaitFor.waitElementVisible(BaseSetup.driver,
        By.id("name"));
        String validationMessage = nameFieldError.getAttribute("validationMessage");
        Assert.assertEquals(validationMessage, "Please fill out this field.",
        "Không có thông báo lỗi khi để trống tên danh mục.");
        return; // Kết thúc nếu có lỗi
        }
        // Kiểm tra thông báo lỗi nếu không chọn ảnh
        try {
        WebElement errorMessage = WaitFor.waitElementVisible(BaseSetup.driver,
        By.id("error-message"));
        String errorText = errorMessage.getText();
        Assert.assertTrue(errorText.contains("Vui lòng chọn ảnh cho danh mục."),
        "Không có thông báo lỗi phù hợp khi không chọn ảnh.");
        } catch (TimeoutException e) {
        WebElement successToast = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(
                "//div[@class='toast success']/div[@class='content']/span[text()='Thêm mới danh mục thành công']"));
        String successMessage = successToast.getText();
        Assert.assertEquals(successMessage, "Thêm mới danh mục thành công", "Thông báo không khớp.");
        }
    }

}
