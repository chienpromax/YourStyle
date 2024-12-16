package yourstyle.com.shope.pages;

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

import yourstyle.com.shope.untils.WaitFor;

public class CategoryPage {
    private static WebDriver driver;
    private WebDriverWait wait;

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openAddCategoryPage() {
        driver.get("http://localhost:8080/admin/categories/list");
    }

    // xoas
    public static void deleteCategory(int page, String valueDelete) {
        Actions actions = new Actions(driver);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String menuCategory = "//span[text()='Quản lý danh mục']";
        String clickCategory = "//span[text()='Danh mục']";
        String clickPage = "//li[@class='page-item']/a[text()='" + page + "']";
        String btnDelete = "//tbody/tr/td[text()='" + valueDelete + "']/../td[@class='text-nowrap']/button";
        String confirmDelete = "//div[@class='modal fade show']/div/div/div[@class='modal-footer']/a";
        String resultDelete = "//span[text()='Xóa danh mục thành công']";
        String checkDeleteSucces = "//tbody/tr/td[(@class='text-truncate category-name') and (text()='" + valueDelete
                + "')]";
        WebElement clickMenu = WaitFor.waitElementVisible(driver, By.xpath(menuCategory));
        clickMenu.click();
        WebElement clickMenuCategory = WaitFor.waitElementVisible(driver, By.xpath(clickCategory));
        clickMenuCategory.click();

        // Cuộn xuống 3 lần trước khi click vào trang
        for (int i = 0; i < 3; i++) {
            jsExecutor.executeScript("window.scrollBy(0, document.body.scrollHeight)");
            try {
                Thread.sleep(1000); // Chờ để nội dung tải
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        WebElement clickPagee = WaitFor.waitElementVisible(driver, By.xpath(clickPage));
        clickPagee.click();
        // actions.moveToElement(clickPagee).click().build().perform();

        WebElement clickDelete = WaitFor.waitElementVisible(driver, By.xpath(btnDelete));
        clickDelete.click();
        WebElement clickConfirmDelete = WaitFor.waitElementVisible(driver, By.xpath(confirmDelete));
        clickConfirmDelete.click();
        WebElement actualDelete = WaitFor.waitElementVisible(driver, By.xpath(resultDelete));
        String actualDeleteSucess = actualDelete.getText();
        Assert.assertEquals(actualDeleteSucess, "Xóa danh mục thành công",
                "Kết quả mong đợi và kết quả thực tết không giống nhau");
        // check còn tồn tại không : lỗi chỗ ni nè
        try {
            driver.findElement(By.xpath(checkDeleteSucces));
            System.out.println("Danh mục vẫn còn tồn tại!");
        } catch (NoSuchElementException ex) {
            System.out.println("Danh mục đã được xóa");
        }

    }

    public static void addCategory(String name, String parentCategory, String imagePath) {
        driver.get("http://localhost:8080/admin/categories/list");

        WebElement nameField = WaitFor.waitElementVisible(driver, By.id("name"));
        nameField.clear();
        nameField.sendKeys(name);

        Select parentCategorySelect = new Select(driver.findElement(By.id("categoryParent")));
        parentCategorySelect.selectByVisibleText(parentCategory);

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("File không tồn tại: " + imagePath);
            }
            WebElement imageField = driver.findElement(By.id("imageInput"));
            imageField.sendKeys(imageFile.getAbsolutePath());
        }

        // Chờ nút "Thêm" có thể nhấp
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement addButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Thêm')]")));
        // Cuộn vào vị trí nút (nếu cần)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
        // Nhấp nút bằng JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);

        // Kiểm tra trường hợp để trống tên danh mục
        if (name == null || name.trim().isEmpty()) {
            WebElement nameFieldError = WaitFor.waitElementVisible(driver, By.id("name"));
            String validationMessage = nameFieldError.getAttribute("validationMessage");
            Assert.assertEquals(validationMessage, "Please fill out this field.",
                    "Không có thông báo lỗi khi để trống tên danh mục.");
            return; // Kết thúc nếu có lỗi
        }
        // Kiểm tra thông báo lỗi nếu không chọn ảnh
        try {
            WebElement errorMessage = WaitFor.waitElementVisible(driver, By.id("error-message"));
            String errorText = errorMessage.getText();
            Assert.assertTrue(errorText.contains("Vui lòng chọn ảnh cho danh mục."),
                    "Không có thông báo lỗi phù hợp khi không chọn ảnh.");
        } catch (TimeoutException e) {
            WebElement successToast = WaitFor.waitElementVisible(driver, By.xpath(
                    "//div[@class='toast success']/div[@class='content']/span[text()='Thêm mới danh mục thành công']"));
            String successMessage = successToast.getText();
            Assert.assertEquals(successMessage, "Thêm mới danh mục thành công", "Thông báo không khớp.");
        }
    }

    public static void editCategory(int categoryId, String name, String parentCategory, String imagePath) {
        driver.get("http://localhost:8080/admin/categories/edit/" + categoryId);

        WebElement categoryNameField = WaitFor.waitElementVisible(driver, By.id("name"));
        categoryNameField.clear();
        categoryNameField.sendKeys(name);

        Select parentCategorySelect = new Select(driver.findElement(By.id("categoryParent")));
        parentCategorySelect.selectByVisibleText(parentCategory);

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IllegalArgumentException("File không tồn tại: " + imagePath);
            }
            WebElement imageField = driver.findElement(By.id("imageInput"));
            imageField.sendKeys(imageFile.getAbsolutePath());
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement updateButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Cập nhật')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", updateButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", updateButton);

        WebElement successToast = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                        "//div[@class='toast success']/div[@class='content']/span[text()='Cập nhật thành công']")));
        String successMessage = successToast.getText();
        Assert.assertEquals(successMessage, "Cập nhật thành công", "Thông báo không khớp.");
    }

   

}
