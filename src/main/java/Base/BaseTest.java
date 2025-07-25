package Base;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.testng.annotations.*;

	public class BaseTest {

	    protected WebDriver driver;

	    @BeforeMethod(alwaysRun = true)
	    public void setup() throws Exception {
	        driver = new ChromeDriver();
	        driver.manage().window().maximize();
	    }

	   @Test
	    public void teardown() {
	        if (driver != null) {
	            driver.quit();
	        }
	    }
	    @AfterMethod(alwaysRun = true)
	    public WebDriver getDriver() {
	        return driver;
	    }
	}

