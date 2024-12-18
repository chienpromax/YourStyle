package yourstyle.com.shope.untils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import yourstyle.com.shope.base.BaseSetup;

import java.time.Duration;

public class WaitFor {
    public static WebElement waitElementVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // visibilityElementLocated check element exist in dom and see if it show
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitElementClick(WebDriver driver, By locator) {
       WebDriverWait wait = new WebDriverWait(BaseSetup.driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // Optional: Wait until an element is invisible
    public static boolean waitElementInvisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

}
