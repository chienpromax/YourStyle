package yourstyle.com.shope.page;


import org.openqa.selenium.*;
import org.testng.Assert;
import yourstyle.com.shope.base.BaseSetup;
import yourstyle.com.shope.untils.WaitFor;

public class LoginPage {
    WebDriver driver;

    static String inputUserName = "//input[@name='username']";
    static String inputPassword = "//input[@name='password']";
    //element error
    static String actucalResult = "//div[contains(@class,'swal2-popup swal2-modal swal2-icon-warning swal2-show')]/div[text()='Tên đăng nhập hoặc mật khẩu không đúng.']";

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public static void login(String username, String password) {
        try {
            WebElement ipUserName = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputUserName));
            ipUserName.sendKeys(username);
            WebElement ipPassWord = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputPassword));
            ipPassWord.sendKeys(password);
            ipPassWord.sendKeys(Keys.ENTER);
            Thread.sleep(5000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Invalid username or password
    public static void loginInvalid(String username, String password) {
        try {
            login(username, password);
            WebElement actualResultInvalid = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(actucalResult));
            String actualResultNotice = actualResultInvalid.getText();
            System.out.println("Actual Result" + actualResultNotice);
            Assert.assertEquals(actualResultNotice, "Tên đăng nhập hoặc mật khẩu không đúng.", "Actual results and expected results are not the same");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //Invalid  empty username
    public static void loginInvalidEmptyUserName(String username, String password) {
        try {
            WebElement ipUserName = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputUserName));
            ipUserName.sendKeys(username);
            WebElement ipPassWord = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputPassword));
            ipPassWord.sendKeys(password);
            ipPassWord.sendKeys(Keys.ENTER);
            String validationMessage = ipUserName.getAttribute("validationMessage");
            System.out.println(validationMessage);
            Assert.assertEquals(validationMessage, "Please fill out this field.", "The validation message of Email field not match.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Invalid  empty password
    public static void loginInvalidEmptyPassWord(String username, String password) {
        try {
            WebElement ipUserName = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputUserName));
            ipUserName.sendKeys(username);
            WebElement ipPassWord = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(inputPassword));
            ipPassWord.sendKeys(password);
            ipPassWord.sendKeys(Keys.ENTER);
            String validationMessage =  ipPassWord.getAttribute("validationMessage");
            System.out.println(validationMessage);
            Assert.assertEquals(validationMessage, "Please fill out this field.", "The validation message of Email field not match.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
