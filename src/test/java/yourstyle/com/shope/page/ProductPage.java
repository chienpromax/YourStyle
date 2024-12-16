package yourstyle.com.shope.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import yourstyle.com.shope.base.BaseSetup;
import yourstyle.com.shope.untils.InputValidation;
import yourstyle.com.shope.untils.ScrollTo;
import yourstyle.com.shope.untils.WaitFor;

public class ProductPage {
    static String menuProduct = "//span[text()='Quản lý sản phẩm']";
    static String clickProduct = "//span[text()='Sản phẩm']";
    static String createProduct = "//div/a[@id='addStaff']";
    static String inputNameProduct = "//div/input[@placeholder='Tên sản phẩm']";
    static String inputPriceProduct = "//div/input[@placeholder='Giá']";
    static String selectCategory = "//select[@id='categorySelect']";
    static String inputProductDetail = "//textarea[@name='productDetail']";
    static String inputProductDescription = "//textarea[@name='description']";

    static String btnCreate = "//button[@class='btn btn-primary rounded-pill py-2 px-4 me-3']";

    public static void insertProductSuccess(String nameProduct, String priceProduct, int categoryProduct,
            String productDetail, String productDescription, String status) {
        Actions actions = new Actions(BaseSetup.driver);
        WebElement clickMenu = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(menuProduct));
        clickMenu.click();
        WebElement clickMenuProduct = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(clickProduct));
        clickMenuProduct.click();
        WebElement clickButtonCreate = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(createProduct));
        clickButtonCreate.click();
        InputValidation.clearInput(BaseSetup.driver, nameProduct, By.xpath(inputNameProduct));
        InputValidation.clearInput(BaseSetup.driver, priceProduct, By.xpath(inputPriceProduct));
        selectCategoryProduct(categoryProduct);
        WebElement sendProductDetail = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputProductDetail));
        sendProductDetail.sendKeys(productDetail);
        WebElement inputDescription = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputProductDescription));
        inputDescription.sendKeys(productDescription);
        checkStatusProduct(status);
        WebElement clickBtnCreate = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(btnCreate));
        clickBtnCreate.click();
    }

    public static void checkStatusProduct(String status) {
        Actions actions = new Actions(BaseSetup.driver);
        String statusProduct = "//div[@class='row container']/div/input[@value='" + status + "']";
        WebElement element = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(statusProduct));
        actions.moveToElement(BaseSetup.driver
                .findElement(By.xpath("/html/body/section/main/section/div/div/div/div/div[2]/div[1]/form/h4"))).build()
                .perform();
        // ScrollTo.element(element);
        element.click();

    }

    public static void selectCategoryProduct(int categoryId) {
        WebElement clickCategory = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(selectCategory));
        clickCategory.click();
        String xpath = "//select/option[@value='" + categoryId + "']";
        WebElement choiceCategory = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(xpath));
        ScrollTo.element(choiceCategory);
        choiceCategory.click();

    }

}
