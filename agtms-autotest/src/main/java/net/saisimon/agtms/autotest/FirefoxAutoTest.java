package net.saisimon.agtms.autotest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class FirefoxAutoTest extends AbstractAutoTest {
	
	@Value("${auto.test.firefox.driver}")
	private String driver;

	@Override
	public WebDriver createDriver() {
		if (StringUtils.isEmpty(driver)) {
			return null;
		}
		System.setProperty("webdriver.gecko.driver", driver);
		return new FirefoxDriver();
	}

}
