package net.saisimon.agtms.autotest;

import org.openqa.selenium.WebDriver;

public interface AutoTest {
	
	default void autoTest() throws Exception {
		WebDriver driver = createDriver();
		if (driver == null) {
			return;
		}
		try {
			doTest(driver);
		} catch (Exception e) {
			throw e;
		} finally {
			detroyDriver(driver);
		}
	}
	
	WebDriver createDriver();
	
	void doTest(WebDriver driver) throws Exception;
	
	void detroyDriver(WebDriver driver);
	
}
