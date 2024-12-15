package yourstyle.com.shope.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import yourstyle.com.shope.base.BaseSetup;
import yourstyle.com.shope.untils.InputValidation;
import yourstyle.com.shope.untils.WaitFor;

public class UserProfilePage {
    //xpath
    static String clickProfile = "//img[@class='rounded-circle']";
    static String clickUsertoPage = "//li/a[contains(@href,'/yourstyle/accounts/profile?type=')]";
    static String choiceImage = "//div/img[@class='rounded-circle border border-dark border-1 img-fluid']";
    static String inputFullName = "//input[@name='fullname']";
    static String inputPhoneNumber = "//input[@name='phoneNumber']";
    static String btnUpdate = "//button[contains(text(),'Cập nhật thông tin')]";

    public static void UserProfileDetailInsertSuccess(String filePath, String gender, String fullName, String PhoneNumber) {
            WebElement clickPro = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(clickProfile));
            clickPro.click();
            WebElement clickUserName = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(clickUsertoPage));
            clickUserName.click();
            WebElement clickAvatar = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(choiceImage));
//            String filePath = "Downloads\\anh-cua-shin-1.jpg";
            clickAvatar.sendKeys(filePath);
            checkGender(gender);
            InputValidation.clearInput(BaseSetup.driver, fullName, By.xpath(inputFullName));
            InputValidation.clearInput(BaseSetup.driver, PhoneNumber, By.xpath(inputPhoneNumber));
            WebElement clickSave = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(btnUpdate));
            clickSave.click();
    }

    public static void checkGender(String gender) {
        String xpath = "//input[@value='" + gender + "']";
        WebElement clickRadioGender = WaitFor.waitElementVisible(BaseSetup.driver, By.xpath(xpath));
        clickRadioGender.click();
    }
}
