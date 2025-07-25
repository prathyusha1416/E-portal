package TestCases;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import Base.BaseTest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AutomateleavesinTestng extends BaseTest {
    
    WebDriverWait wait;
    String[][] users = {
        {"akhila.n", "asdASD@123"},
        {"anusha.j", "asdASD@123"}
    };

    @BeforeClass
    public void setups() throws Exception {
        Files.createDirectories(Paths.get("screenshots"));
    }

    @Test
    public void applyLeavesForUsers() throws InterruptedException {
        for (String[] user : users) {
            
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                driver.get("http://192.168.0.59:8081/E-portal/");
                driver.manage().window().maximize();
                driver.findElement(By.id("username")).sendKeys(user[0]);
                driver.findElement(By.id("password")).sendKeys(user[1]);
                driver.findElement(By.xpath("//button[@class='btn btn-primary btn-block']")).click();
                Thread.sleep(2000);

                LocalDate baseDate = LocalDate.now().plusDays(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                int validLeaveIndex = 0;
                List<String> leaveTypes = getLeaveTypes();

                for (String leaveType : leaveTypes) {
                    if (leaveType.contains("Select") || leaveType.contains("Maternity") || leaveType.contains("Paternity"))
                        continue;

                    try {
                        driver.findElement(By.xpath("//span[text()='Dashboard']")).click();
                        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leaves & Permissions']"))).click();
                        wait.until(ExpectedConditions.elementToBeClickable(By.id("leavebal"))).click();
                    } catch (Exception e) {
                        System.out.println("[" + user[0] + "] Failed to reload leave form.");
                        break;
                    }

                    WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.name("leave_type")));
                    Select select = new Select(dropdown);
                    select.selectByVisibleText(leaveType);

                    LocalDate leaveDate = baseDate.plusDays(validLeaveIndex);
                    String dateStr = leaveDate.format(formatter);

                    WebElement fromDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leavefdate")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + dateStr + "';", fromDate);

                    WebElement days = driver.findElement(By.id("no_ofdays"));
                    days.clear();
                    days.sendKeys("1");

                    WebElement reason = driver.findElement(By.id("textArea"));
                    reason.clear();
                    reason.sendKeys("Auto leave test for " + leaveType);

                    WebElement submit = driver.findElement(By.xpath("//button[text()='Submit']"));
                    submit.click();

                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alert = driver.switchTo().alert();
                    System.out.println("Alert: " + alert.getText());
                    alert.accept();
                    validLeaveIndex++;
                    Thread.sleep(1000);
                }

                try {
                    driver.findElement(By.xpath("//a[@role='button']")).click();
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Logout')]"))).click();
                } catch (Exception e) {
                    System.out.println("Logout skipped due to session error.");
                }
            } finally {
                driver.quit();
            }
        }
    }

    public List<String> getLeaveTypes() throws InterruptedException {
        driver.findElement(By.xpath("//span[text()='Dashboard']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leaves & Permissions']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("leavebal"))).click();
        Thread.sleep(1000);
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("leave_type"))));
        return select.getOptions().stream()
                .filter(opt -> opt.isEnabled() && opt.getAttribute("disabled") == null)
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
} 
