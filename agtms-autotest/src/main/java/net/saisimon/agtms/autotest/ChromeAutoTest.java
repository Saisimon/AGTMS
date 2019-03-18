package net.saisimon.agtms.autotest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class ChromeAutoTest extends AbstractAutoTest {

	@Value("${auto.test.chrome.driver}")
	private String driver;
	
	@Override
	public WebDriver createDriver() {
		if (StringUtils.isEmpty(driver)) {
			return null;
		}
		System.setProperty("webdriver.chrome.driver", driver);
		return new ChromeDriver();
	}

}
