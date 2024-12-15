package yourstyle.com.shope.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import yourstyle.com.shope.untils.ReadConfig;


public class BaseSetup {
    public static WebDriver driver;

    public static void setupDriver() {
        String browser = ReadConfig.getProperty("browser");
        System.out.println("hi " + browser);
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                driver.get(ReadConfig.getProperty("url"));
                driver.manage().window().maximize();
                break;
            case "edge":
                driver = new EdgeDriver();
                driver.get(ReadConfig.getProperty("url"));
                driver.manage().window().maximize();
                break;
            default:
                System.out.println("Please enter browser chrome or edge");
        }

    }
}
