package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void createOrderThenVisibleInHistory(ChromeDriver driver) {
        String uniqueAuthor = "Safira-" + UUID.randomUUID();
        String productName = "Keyboard";

        driver.get(baseUrl + "/order/create");

        driver.findElement(By.id("authorInput")).sendKeys(uniqueAuthor);
        driver.findElement(By.id("productNameInput")).sendKeys(productName);
        driver.findElement(By.id("productQtyInput")).sendKeys("2");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement orderIdEl = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("orderIdValue"))
        );
        String orderId = orderIdEl.getText();

        driver.get(baseUrl + "/order/history");
        driver.findElement(By.id("historyAuthorInput")).sendKeys(uniqueAuthor);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(orderId));
    }
}
