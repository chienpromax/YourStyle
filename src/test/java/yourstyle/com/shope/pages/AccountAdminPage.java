package yourstyle.com.shope.pages;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

public class AccountAdminPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By usernameField = By.id("floatingtennguoidung");
    private final By passwordField = By.id("floatingmatkhau");
    private final By emailField = By.id("floatingemail");
    private final By roleDropdown = By.id("role");
    private final By createButton = By.xpath("//button[contains(text(), 'Tạo tài khoản')]");
    private final By updateButton = By.xpath("//button[contains(text(), 'Cập nhật')]");
    private final By notificationMessage = By.cssSelector(".is-invalid small.text-danger ");
    private final By successToast = By.xpath("//div[@class='toast success']/div[@class='content']/span[text()='Thêm tài khoản thành công']");
    private final By searchField = By.cssSelector(".form-control");


    // Constructor
    public AccountAdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Utility Methods
    private void waitForElementToBeClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void waitForElementToBeVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Add Account Methods
    public void openAddAccountPage() {
        driver.get("http://localhost:8080/admin/accounts/add");
    }
    public void openSearchAccountPage() {
        driver.get("http://localhost:8080/admin/accounts");
    }

    public void fillAccountForm(String username, String password, String email, String role) {
        // Fill username
        WebElement usernameFieldElement = driver.findElement(usernameField);
        usernameFieldElement.clear();
        usernameFieldElement.sendKeys(username);

        // Fill password (if not null)
        if (password != null) {
            WebElement passwordFieldElement = driver.findElement(passwordField);
            passwordFieldElement.clear();
            passwordFieldElement.sendKeys(password);
        }

        // Fill email
        WebElement emailFieldElement = driver.findElement(emailField);
        emailFieldElement.clear();
        emailFieldElement.sendKeys(email);

        // Select role
        Select roleSelect = new Select(driver.findElement(roleDropdown));
        roleSelect.selectByVisibleText(role);
    }

    public void clickCreateButton() {
        try {
            // Chờ nút "Tạo tài khoản" sẵn sàng
            waitForElementToBeClickable(createButton);
    
            // Cuộn tới nút "Tạo tài khoản"
            scrollToElement(createButton);
    
            // Kiểm tra và ẩn các phần tử cản trở nếu có
            try {
                WebElement notification = driver.findElement(By.className("notification-item"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", notification);
            } catch (NoSuchElementException e) {
                System.out.println("No blocking notification found.");
            }
    
            // Nhấp vào nút "Tạo tài khoản" bằng JavaScript để tránh lỗi click interception
            WebElement createButtonElement = driver.findElement(createButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createButtonElement);
        } catch (Exception e) {
            throw e;
        }
    }
    

    public void verifySuccessToast() {
        waitForElementToBeVisible(successToast);
        String actualResult = driver.findElement(successToast).getText();
        Assert.assertEquals(actualResult, "Thêm tài khoản thành công", 
            "The actual result and expected result do not match.");
    }

    // Edit Account Methods
    public void openEditAccountPage(int accountId) {
        driver.get("http://localhost:8080/admin/accounts/edit/" + accountId);
    }
    

    public void clickUpdateButton() {
        waitForElementToBeClickable(updateButton);
        driver.findElement(updateButton).click();
    }

    public String getNotificationMessage() {
        try {
            waitForElementToBeVisible(notificationMessage);
            return driver.findElement(notificationMessage).getText();
        } catch (NoSuchElementException e) {
            return ""; // No notification found
        }
    }


    public void searchAccount(String keyword) {
        WebElement searchInput = driver.findElement(searchField);
        searchInput.clear();
        searchInput.sendKeys(keyword);
        searchInput.submit(); // Gửi form tìm kiếm
    }
    
    public boolean isAccountDisplayed(String username) {
        try {
            WebElement accountElement = driver.findElement(By.xpath("//td[text()='" + username + "']"));
            return accountElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false; // Không tìm thấy tài khoản
        }
    }
    
}
