package TestCases;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Base.BaseTest;

public class ManagerLogin extends BaseTest{

	    WebDriverWait wait;
	    String[][] users = {
	        {"satish.d", "asdASD@123"}
	        
	    };

	    @BeforeClass
	    public void setupss() throws Exception {
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
	                driver.findElement(By.xpath("//span[text()='Dashboard']")).click();
	                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leaves & Permissions']"))).click();
	                driver.findElement(By.xpath("//a[text()='EmpLeaveRequest']")).click();
	                WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("year")));
                    Select select = new Select(dropdown);
                    select.selectByIndex(2);
                    driver.findElement(By.xpath("//button[@type=\"submit\"]")).click();
                    driver.findElement(By.xpath("(//a[text()=' view'])[1]")).click();
                    Thread.sleep(2000);
                    WebElement statusdropdown = wait.until(ExpectedConditions.elementToBeClickable(By.name("mgr_approval")));
                    Select select1 = new Select(statusdropdown);
                    select1.selectByIndex(1);
                    driver.findElement(By.name("mgr_comments")).sendKeys("Test");
                    driver.findElement(By.id("submit")).click();
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alert = driver.switchTo().alert();
                    System.out.println("Alert: " + alert.getText());
                    alert.accept();
                    try {
                        driver.findElement(By.xpath("//a[@role='button']")).click();
                        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Logout')]"))).click();
                    } catch (Exception e) {
                        System.out.println("Logout skipped due to session error.");
                    }
                   
	            }
	            
	            finally {
	              //  driver.quit();
	            }
	        }
	    }
	}

