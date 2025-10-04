package com.harel.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementUtils {

    private WebDriver driver;
    private static final Logger log = LoggerFactory.getLogger(ElementUtils.class);

    public ElementUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Waits until element is visible 
     * If not visible → RuntimeException
     */
    public void assertElementVisible(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            log.info("ASSERT Passed: The element {} is exist and visible", locator);
        } catch (Exception e) {
            log.error("ASSERT Failed: The element {} isn't exist or not visible", locator);
            throw new RuntimeException("Element not visible: " + locator, e);
        }
    }

    // Non-waiting version - before click on element
    public void clickElement(By locator, int seconds) {
        this.clickElementWithScroll(locator, seconds, 1);
    }

    /**
     * Waits until element clickable and click
     * Scorlling to element if not visible + logs
     */
    public void clickElementWithScroll(By locator, int seconds, int maxScrolls) {
        int scrolls = 0;
        boolean clicked = false;

        while (scrolls < maxScrolls) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                // Check if element visible on screen
                if (element.isDisplayed() && element.isEnabled()) {
                    // Scrolling to element and move focus
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                    new Actions(driver).moveToElement(element).perform();

                    // Click
                    element.click();
                    log.info(" Clicking on element was successful {}", locator);
                    clicked = true;
                    break;
                } else {
                    // Sort scrolling if element is exist but not visible
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300);");
                    Thread.sleep(500); // Short witing
                }

            } catch (NoSuchElementException | TimeoutException e) {
                // If element yet exist, little scolling down
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 300);");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            } catch (Exception e) {
                log.error(" Clicking on element failed {}", locator, e);
                throw new RuntimeException("Cannot click element: " + locator, e);
            }
            scrolls++;
        }

        if (!clicked) {
            throw new RuntimeException("Clicking on element failed " + locator + " after " + maxScrolls + " scrolls");
        }
    }

    /**
     * Nativ to correct month and selecet return date
     */
    public void selectReturnDate(String returnDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate returnDate = LocalDate.parse(returnDateStr, formatter);

        By nextButton = By.cssSelector("button[aria-label='לעבור לחודש הבא']");
        By monthTitle = By.cssSelector(".MuiPickersCalendarHeader-transitionContainer p");
        By dayButton = By.cssSelector("button[data-hrl-bo='" + returnDate + "']");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nativ to correct month
        while (true) {
            String currentMonth = wait.until(ExpectedConditions.visibilityOfElementLocated(monthTitle)).getText();

            // Check month
            if (currentMonth.equals(returnDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("he"))))) {
                break;
            }
            // Waits until button is able + scrolling
            WebElement next = wait.until(ExpectedConditions.presenceOfElementLocated(nextButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", next);
            wait.until(ExpectedConditions.attributeToBe(next, "aria-disabled", "false"));

            try {
                next.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", next);
            }
        }

        // Select day
        WebElement day = wait.until(ExpectedConditions.elementToBeClickable(dayButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", day);
        day.click();

        log.info("Return date successfully selected " + returnDate);
    }

    /**
     * If element is exist -> return text's element
     * If not exist -> RuntimeException
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

    // Non-waiting varsion -  before return text's element
    public String getTextIfVisible(By locator) {
        return getTextIfVisible(locator, 0);
    }

}
