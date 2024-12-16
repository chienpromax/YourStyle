package yourstyle.com.shope.untils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InputValidation {
    public static void clearInput(WebDriver driver, String value, By locator) {
        WebElement input = WaitFor.waitElementVisible(driver, locator);
        input.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        input.sendKeys(value);
    }
}
