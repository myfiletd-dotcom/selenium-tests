package com.harel.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementUtils {

    private WebDriver driver;
    private static final Logger log = LoggerFactory.getLogger(ElementUtils.class);

    private static final Map<String, String> MONTHS_HEB = Map.ofEntries(
            Map.entry("JANUARY", "ינואר"),
            Map.entry("FEBRUARY", "פברואר"),
            Map.entry("MARCH", "מרץ"),
            Map.entry("APRIL", "אפריל"),
            Map.entry("MAY", "מאי"),
            Map.entry("JUNE", "יוני"),
            Map.entry("JULY", "יולי"),
            Map.entry("AUGUST", "אוגוסט"),
            Map.entry("SEPTEMBER", "ספטמבר"),
            Map.entry("OCTOBER", "אוקטובר"),
            Map.entry("NOVEMBER", "נובמבר"),
            Map.entry("DECEMBER", "דצמבר"));

    public ElementUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * מחכה שהאלמנט יהיה מוצג.
     * אם לא מוצג → RuntimeException
     */
    public void assertElementVisible(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            log.info("ASSERT Passed: האלמנט {} קיים ומוצג", locator);
        } catch (Exception e) {
            log.error("ASSERT Failed: האלמנט {} לא נמצא או לא מוצג", locator);
            throw new RuntimeException("Element not visible: " + locator, e);
        }
    }

    /**
     * מחכה שהאלמנט יהיה לחיץ ומבצעת עליו לחיצה, עם לוג.
     */
    public void clickElement(By locator, int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

            // נגלול עד לאלמנט
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

            // נוודא שהאלמנט באמת בפריים ונראה
            new Actions(driver).moveToElement(element).perform();
            // click
            element.click();
            log.info("✔ לחיצה בוצעה בהצלחה על האלמנט {}", locator);
        } catch (Exception e) {
            log.error("❌ לא הצלחנו ללחוץ על האלמנט {}", locator, e);
            throw new RuntimeException("Cannot click element: " + locator, e);
        }
    }

    /**
     * בוחרת תאריך חזרה בלוח השנה
     */
    public void selectReturnDate(String returnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate returnDate = LocalDate.parse(returnDateStr, formatter);

        int day = returnDate.getDayOfMonth();
        String monthHeb = MONTHS_HEB.get(returnDate.getMonth().name()) + " " + returnDate.getYear();

        while (true) {
            WebElement currentMonthElem = driver.findElement(By.cssSelector(".calendar-header .month"));
            String currentMonth = currentMonthElem.getText();

            if (currentMonth.equalsIgnoreCase(monthHeb)) {
                break;
            } else {
                WebElement nextButton = driver.findElement(By.cssSelector(".calendar-next"));
                nextButton.click();
                log.info("לחצנו על החץ הבא בלוח השנה כדי להגיע לחודש {}", monthHeb);
            }
        }
        WebElement dayElem = driver.findElement(By.xpath("//td[@data-day='" + day + "']"));
        dayElem.click();
        log.info("בחרנו את היום {}", day);
    }

    /**
     * שולף טקסט אם האלמנט קיים ומוצג.
     * אם לא → RuntimeException
     */
    public String getTextIfVisible(By locator, int seconds) {
        try {
            WebElement element;
            if (seconds > 0) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
                element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            } else {
                List<WebElement> elements = driver.findElements(locator);
                if (elements.isEmpty() || !elements.get(0).isDisplayed()) {
                    throw new RuntimeException("Element not visible: " + locator);
                }
                element = elements.get(0);
            }
            String text = element.getText();
            log.info("Element found: {} | Text: {}", locator, text);
            return text;
        } catch (Exception e) {
            log.error("Error finding element: {}", locator, e);
            throw new RuntimeException("Error finding element: " + locator, e);
        }
    }

    // גרסה ללא זמן המתנה
    public String getTextIfVisible(By locator) {
        return getTextIfVisible(locator, 0);
    }
}
