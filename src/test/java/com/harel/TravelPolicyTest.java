package com.harel;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.harel.utils.StringUtils;
import com.harel.utils.DateUtils;
import com.harel.utils.ElementUtils;

public class TravelPolicyTest {

    private WebDriver driver;
    private ElementUtils elementUtils;

    @BeforeClass
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\drivers\\chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        elementUtils = new ElementUtils(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://digital.harel-group.co.il/travel-policy");
    }

    @Test
    public void travelInsuranceFirstTimeTest() {
        // Click on buy Travel Insurance First Time button
        elementUtils.clickElement(By.cssSelector("div.jss11 > button"), 5);

        // Select destination
        elementUtils.clickElementWithScroll(By.xpath("//div[@class='jss174 jss176']//div[text()='אסיה']"), 5, 3);

        // Continue to the next level
        elementUtils.clickElementWithScroll(By.cssSelector("[data-hrl-bo='wizard-next-button']"), 5, 3);

        // Select exit date (today)
        String todayString = DateUtils.getTodayDate();
        elementUtils.clickElement(By.cssSelector("button[data-hrl-bo='" + todayString + "']"), 5);

        // Select return date
        int expectedDays = 30;
        String returnDate = DateUtils.getDateFromToday(expectedDays);
        elementUtils.selectReturnDate(returnDate);

        // Assert total days
        String totalDaysStr = elementUtils.getTextIfVisible(By.cssSelector("[data-hrl-bo='total-days']"), 5);
        int actualDays = StringUtils.getDaysFromString(totalDaysStr);
        Assert.assertEquals(actualDays, expectedDays, "The number of days does'nt match");

        // Go to next screen
        elementUtils.clickElement(By.id("nextButton"), 5);

        // Next screen has appeared
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement newPageElement = wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(By.cssSelector("[data-hrl-bo='traveler-card-contact-person']")));
        Assert.assertTrue(newPageElement.isDisplayed(), "The new page did'nt load!");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
