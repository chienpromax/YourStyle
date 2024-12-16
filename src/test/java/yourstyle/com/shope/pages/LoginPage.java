package yourstyle.com.shope.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.cssSelector(".custom-btn-main");

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Methods for interacting with elements
    public void enterUsername(String username) {
        WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        usernameElement.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement passwordElement = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        passwordElement.sendKeys(password);
    }

    public void clickLoginButton() {
        WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));
        // Scroll to the button to ensure it is visible
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginElement);
        wait.until(ExpectedConditions.elementToBeClickable(loginElement));

        try {
            loginElement.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginElement);
        }
    }

    public boolean isOnHomePage(String expectedUrl) {
        wait.until(ExpectedConditions.urlToBe(expectedUrl));
        return driver.getCurrentUrl().equals(expectedUrl);
    }
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
}
