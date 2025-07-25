package TestCases;

import Utilities.ExcelReader;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class MainLeaveAutomationExcel {
    public static void main(String[] args) throws IOException {
        List<String[]> credentials = ExcelReader.readCredentials("credentials.xlsx");

        for (String[] user : credentials) {
            String username = user[0];
            String password = user[1];

            WebDriver driver = new ChromeDriver();
            driver.manage().window().maximize();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            try {
                driver.get("http://192.168.0.59:8081/E-portal/");
                driver.findElement(By.id("username")).sendKeys(username);
                driver.findElement(By.id("password")).sendKeys(password);
                driver.findElement(By.xpath("//button[@class='btn btn-primary btn-block']")).click();

                // Navigate to Leaves & Permissions
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Dashboard']"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leaves & Permissions']"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.id("leavebal"))).click();

                int dateOffset = 1;

               
                while (true) {
                    Select leaveSelect = new Select(wait.until(ExpectedConditions.presenceOfElementLocated(By.name("leave_type"))));
                    List<WebElement> leaveOptions = leaveSelect.getOptions();

                    boolean leaveApplied = false;

                    for (WebElement option : leaveOptions) {
                        String leaveType = option.getText();
                        if (leaveType.contains("--Select") || !option.isEnabled()) {
                            System.out.println("Skipping leave type: " + leaveType);
                            continue;
                        }

                        try {
                           
                            leaveSelect = new Select(wait.until(ExpectedConditions.presenceOfElementLocated(By.name("leave_type"))));
                            leaveSelect.selectByVisibleText(leaveType);

                            String date = String.format("07/%02d/2025", dateOffset++);

                            
                            WebElement dateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leavefdate")));
                            ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + date + "';", dateField);

                            driver.findElement(By.id("no_ofdays")).clear();
                            driver.findElement(By.id("no_ofdays")).sendKeys("1");
                            driver.findElement(By.id("textArea")).clear();
                            driver.findElement(By.id("textArea")).sendKeys("Auto Test for " + leaveType);

                            driver.findElement(By.xpath("//button[text()='Submit']")).click();

                            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                            System.out.println("[" + username + "] " + leaveType + " | Date: " + date);
                            System.out.println("Alert text: " + alert.getText());
                            alert.accept();

                            leaveApplied = true;

                            
                            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leaves & Permissions']"))).click();
                            wait.until(ExpectedConditions.elementToBeClickable(By.id("leavebal"))).click();

                            Thread.sleep(1000); 
                        } catch (Exception e) {
                            System.out.println("Skipping leave type due to error: " + leaveType + " | " + e.getMessage());
                            continue;
                        }
                    }

                    if (leaveApplied) break; 
                    else System.out.println("No valid leave types available for: " + username);
                }

               
                driver.findElement(By.xpath("//a[@role='button']")).click();
                WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Logout')]")));
                logout.click();

            } catch (Exception ex) {
                System.out.println("Error for user: " + username + " - " + ex.getMessage());
            } finally {
                driver.quit();
            }
        }
    }
}
