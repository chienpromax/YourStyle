package yourstyle.com.shope.page;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import yourstyle.com.shope.base.BaseSetup;
import yourstyle.com.shope.untils.WaitFor;

public class AccountAdminPage {
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

    // Utility methods
    private void scrollToElement(By locator) {
        WebElement element = BaseSetup.driver.findElement(locator);
        ((JavascriptExecutor) BaseSetup.driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Methods for account actions
    public void openAddAccountPage() {
        BaseSetup.driver.get("http://localhost:8080/admin/accounts/add");
    }

    public void openSearchAccountPage() {
        BaseSetup.driver.get("http://localhost:8080/admin/accounts");
    }

    public void fillAccountForm(String username, String password, String email, String role) {
        // Fill username
        WebElement usernameFieldElement = BaseSetup.driver.findElement(usernameField);
        usernameFieldElement.clear();
        usernameFieldElement.sendKeys(username);

        // Fill password (if not null)
        if (password != null) {
            WebElement passwordFieldElement = BaseSetup.driver.findElement(passwordField);
            passwordFieldElement.clear();
            passwordFieldElement.sendKeys(password);
        }

        // Fill email
        WebElement emailFieldElement = BaseSetup.driver.findElement(emailField);
        emailFieldElement.clear();
        emailFieldElement.sendKeys(email);

        // Select role
        Select roleSelect = new Select(BaseSetup.driver.findElement(roleDropdown));
        roleSelect.selectByVisibleText(role);
    }

    public void clickCreateButton() {
        try {
            // Wait for the button to be clickable
            WebElement createButtonElement = WaitFor.waitElementClick(BaseSetup.driver, createButton);

            // Scroll to the button
            scrollToElement(createButton);

            // Click using JavaScript to avoid interception
            ((JavascriptExecutor) BaseSetup.driver).executeScript("arguments[0].click();", createButtonElement);
        } catch (Exception e) {
            throw new RuntimeException("Error clicking the Create button: " + e.getMessage(), e);
        }
    }

    public void verifySuccessToast() {
         WaitFor.waitElementVisible(BaseSetup.driver, successToast);
        String actualResult = BaseSetup.driver.findElement(successToast).getText();
        Assert.assertEquals(actualResult, "Thêm tài khoản thành công", 
            "The actual result and expected result do not match.");
    }

    public String getNotificationMessage() {
        try {
            WebElement notificationElement = WaitFor.waitElementVisible(BaseSetup.driver, notificationMessage);
            return notificationElement.getText();
        } catch (NoSuchElementException e) {
            return ""; // No notification found
        }
    }

    public void searchAccount(String keyword) {
        WebElement searchInput = BaseSetup.driver.findElement(searchField);
        searchInput.clear();
        searchInput.sendKeys(keyword);
        searchInput.submit(); // Submit the search form
    }

    public boolean isAccountDisplayed(String username) {
        try {
            WebElement accountElement = BaseSetup.driver.findElement(By.xpath("//td[text()='" + username + "']"));
            return accountElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false; // Account not found
        }
    }
     // Edit Account Methods
     public void openEditAccountPage(int accountId) {
        BaseSetup.driver.get("http://localhost:8080/admin/accounts/edit/" + accountId);
    }
    public void clickUpdateButton() {
        WebElement waitForElementToBeClickable = WaitFor.waitElementClick(BaseSetup.driver, updateButton);
        BaseSetup.driver.findElement(updateButton).click();
    }
}
