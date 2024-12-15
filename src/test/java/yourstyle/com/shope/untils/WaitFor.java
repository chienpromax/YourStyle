    package yourstyle.com.shope.untils;

    import org.openqa.selenium.By;
    import org.openqa.selenium.WebDriver;
    import org.openqa.selenium.WebElement;
    import org.openqa.selenium.support.ui.ExpectedConditions;
    import org.openqa.selenium.support.ui.WebDriverWait;

    import java.time.Duration;

    public class WaitFor {
        public static WebElement waitElementVisible(WebDriver driver, By locator) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            //visibilityElementLocated check element exist in dom and see if it show
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        }



    }
