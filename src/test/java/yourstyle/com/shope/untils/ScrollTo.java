package yourstyle.com.shope.untils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import yourstyle.com.shope.base.BaseSetup;

public class ScrollTo {
    public static void element(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) BaseSetup.driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static void scrollMultipleTimesToElement(WebElement element, int times) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) BaseSetup.driver;
        for (int i = 0; i < times; i++) {
            jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
