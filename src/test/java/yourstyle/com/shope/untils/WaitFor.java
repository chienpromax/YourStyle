package yourstyle.com.shope.untils;

import java.time.Duration;

import org.bouncycastle.util.Exceptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitFor {
    public static WebElement waitElementVisible(WebDriver driver, By locator ){
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      //chờ element xuất hiện r mới bắt
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
