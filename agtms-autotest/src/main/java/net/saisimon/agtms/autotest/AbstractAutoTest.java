package net.saisimon.agtms.autotest;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAutoTest implements AutoTest {
	
	@Value("${auto.test.url}")
	private String url;
	@Value("${auto.test.interval}")
	private Long interval;
	@Value("${auto.test.timeout}")
	private Long timeout;

	@Override
	public void detroyDriver(WebDriver driver) {
		log.debug("Detroy Chrome Driver");
		if (driver != null) {
			driver.quit();
		}
	}

	@Override
	public void doTest(WebDriver driver) throws Exception {
		if (driver == null) {
			log.warn("Driver Empty");
			return;
		}
		driver.manage().window().maximize();
		log.debug("URL: " + url);
		driver.get(url);
//		register(driver);
//		signout(driver);
		signin(driver);
		navigationManagement(driver);
		templateManagement(driver);
		selectionManagement(driver);
		taskManagement(driver);
		operationManagement(driver);
		signout(driver);
	}
	
	protected void register(WebDriver driver) throws Exception {
		// 跳转注册页面
		to(driver, "/register");
		// 空表单注册提交
		clickButton(driver, "register-btn");
		// 输入用户名
		inputById(driver, "username", "test");
		// 输入电子邮箱
		inputById(driver, "email", "test@test.com");
		// 输入密码
		inputById(driver, "password", "123456");
		// 点击注册按钮
		clickButton(driver, "register-btn");
	}
	
	protected void signout(WebDriver driver) throws Exception {
		// 登出
		clickButton(driver, "signout-link");
	}
	
	protected void signin(WebDriver driver) throws Exception {
		// 登录跳转
		to(driver, "/signin");
		// 空表单登录提交
		clickButton(driver, "signin-btn");
		// 输入用户名
		inputById(driver, "username", "test");
		// 输入密码
		inputById(driver, "password", "123456");
		// 点击登录按钮
		clickButton(driver, "signin-btn");
	}

	protected void navigationManagement(WebDriver driver) throws Exception {
		// 跳转导航管理列表页面
		to(driver, "/navigation/main");
		// 点击导航创建按钮
		clickButton(driver, "create-btn");
		// 设置导航信息
		setNavigation(driver, 1, "random", "TEST", 1);
		// 重置取消
		reset(driver, false);
		// 重置确认
		reset(driver, true);
		// 创建导航信息
		createNavigation(driver, 1, "random", "TEST-1", 2);
		// 重复创建导航信息
		createNavigation(driver, 1, "random", "TEST-1", 3);
		// 批量创建导航信息
		for (int i = 0; i < 9; i++) {
			createNavigation(driver, 1, "random", "TEST-" + (i + 2), i + 3);
		}
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 点击导航创建按钮
		clickButton(driver, "create-btn");
		createNavigation(driver, 2, "link", "TEST-SUB-1", 3);
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// 精确查询
		inputByClass(driver, "title-input", "TEST-1");
		clickButton(driver, "search-btn");
		// 模糊查询
		select(driver, "//div[contains(@class,'title-operator-select')]", 2);
		clickButton(driver, "search-btn");
		// 逗号分隔查询
		inputByClass(driver, "title-input", "TEST-1,TEST-2");
		select(driver, "//div[contains(@class,'title-operator-select')]", 3);
		clickButton(driver, "search-btn");
		// 点击清空按钮
		clickButton(driver, "clear-btn");
		// 点击刷新按钮
		clickButton(driver, "refresh-btn");
		// 下一页
		nextPage(driver);
		// 上一页
		prevPage(driver);
		// 末页
		lastPage(driver);
		// 首页
		firstPage(driver);
		// 点击批量编辑按钮
		clickButton(driver, "batch-edit-btn");
		closeAlert(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		closeAlert(driver);
		// 编辑列表第一条
		clickActionButton(getTableList(driver).get(0), "btn-outline-primary");
		// 设置导航信息
		setNavigation(driver, 2, "bars", "TEST", 100);
		// 重置确认
		reset(driver, true);
		// 创建导航信息
		createNavigation(driver, 2, "bars", "TEST-CHANGE", 100);
		// 返回
		clickButton(driver, "back-btn");
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量编辑按钮
		clickButton(driver, "batch-edit-btn");
		String selectPath = "//div[@class='batch-edit-container']//div[@class='form-container']//div[@class='multiselect']";
		// 选择图标
		select(driver, selectPath, 3);
		inputById(driver, "icon-input", "list");
		// 选择优先级
		select(driver, selectPath, 4);
		inputById(driver, "priority-input", "99");
		// 批量保存
		clickButton(driver, "save-btn");
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		findElement(driver, By.xpath("//div[@class='batch-remove-container']//div[@class='modal-content']//button[@class='btn btn-outline-danger btn-sm']")).click();
	}

	protected void templateManagement(WebDriver driver) throws Exception {
		// 跳转模板管理列表页面
		to(driver, "/template/main");
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// TODO
		
	}
	
	protected void selectionManagement(WebDriver driver) throws Exception {
		// 跳转下拉列表管理列表页面
		to(driver, "/selection/main");
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// TODO
		
	}
	
	protected void taskManagement(WebDriver driver) throws Exception {
		// 跳转任务管理列表页面
		to(driver, "/task/main");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// TODO
		
	}
	
	protected void operationManagement(WebDriver driver) throws Exception {
		// 跳转模板管理列表页面
		to(driver, "/operation/main");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// 选择登入操作类型
		select(driver, "//div[@class='filter-container']//div[contains(@class,'operateType-select')]", 1);
		// 搜索
		clickButton(driver, "search-btn");
		// 清空
		clickButton(driver, "clear-btn");
		// 选择登出操作类型
		select(driver, "//div[@class='filter-container']//div[contains(@class,'operateType-select')]", 2);
		// 搜索
		clickButton(driver, "search-btn");
		// 取消选择登出操作类型
		select(driver, "//div[@class='filter-container']//div[contains(@class,'operateType-select')]", 2);
		// 搜索
		clickButton(driver, "search-btn");
		// 下一页
		nextPage(driver);
		// 上一页
		prevPage(driver);
		// 末页
		lastPage(driver);
		// 首页
		firstPage(driver);
	}
	
	protected void setNavigation(WebDriver driver, int navigationIndex, String icon, String title, int priority) throws Exception {
		select(driver, "//div[@class='edit-container']//div[@class='form-container']//div[contains(@class,'multiselect')]", navigationIndex);
		inputById(driver, "icon-input", icon);
		inputById(driver, "title-input", title);
		inputById(driver, "priority-input", "" + priority);
	}
	
	protected void to(WebDriver driver, String path) throws Exception {
		driver.navigate().to(URLUtil.formatUrl(url + path));
		Thread.sleep(interval);
	}
	
	protected void createNavigation(WebDriver driver, int navigationIndex, String icon, String title, int priority) throws Exception {
		setNavigation(driver, navigationIndex, icon, title, priority);
		clickButton(driver, "save-btn");
		closeAlert(driver);
	}
	
	protected void reset(WebDriver driver, boolean ok) throws Exception {
		clickButton(driver, "reset-btn");
		if (ok) {
			findElement(driver, By.xpath("//div[@id='reset-modal']//div[@class='modal-content']//button[@class='btn btn-outline-danger btn-sm']")).click();
			Thread.sleep(interval);
		} else {
			findElement(driver, By.xpath("//div[@id='reset-modal']//div[@class='modal-content']//button[@class='btn btn-outline-info btn-sm']")).click();
			Thread.sleep(interval);
		}
	}
	
	protected void closeAlert(WebDriver driver) throws Exception {
		WebElement closeElement = findElement(driver, By.xpath("//div[contains(@class,'alert')]/button[@class='close']"));
		if (closeElement == null) {
			return;
		}
		closeElement.click();
		Thread.sleep(interval);
	}
	
	protected void clickButton(WebDriver driver, String className) throws Exception {
		WebElement buttonElement = findElement(driver, By.className(className));
		if (buttonElement == null) {
			return;
		}
		buttonElement.click();
		Thread.sleep(interval);
	}
	
	protected void select(WebDriver driver, String selectPath, int index) throws Exception {
		WebElement selectElement = findElement(driver, By.xpath(selectPath));
		new Actions(driver)
			.click(selectElement)
			.click(findElement(driver, By.xpath(selectPath + "//li[@class='multiselect__element']["+ index +"]")))
			.release(selectElement)
			.perform();
		Thread.sleep(interval);
	}
	
	protected void inputById(WebDriver driver, String idName, String text) throws Exception {
		WebElement element = findElement(driver, By.id(idName));
		element.clear();
		element.sendKeys(text);
	}
	
	protected void inputByClass(WebDriver driver, String className, String text) throws Exception {
		WebElement element = findElement(driver, By.className(className));
		element.clear();
		element.sendKeys(text);
	}
	
	protected WebElement findElement(WebDriver driver, By by) {
		try {
			(new WebDriverWait(driver, timeout / 1000)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					List<WebElement> elements = driver.findElements(by);
					if (elements.size() == 0) {
						return false;
					}
					WebElement element = elements.get(0);
					return element.isEnabled();
				}
			});
		} catch (TimeoutException e) {
			log.warn("find Element Timeout: " + by);
			return null;
		}
		return driver.findElement(by);
	}
	
	protected void firstPage(WebDriver driver) throws Exception {
		page(driver, 0);
	}
	
	protected void prevPage(WebDriver driver) throws Exception {
		page(driver, 1);
	}
	
	protected void nextPage(WebDriver driver) throws Exception {
		page(driver, -2);
	}
	
	protected void lastPage(WebDriver driver) throws Exception {
		page(driver, -1);
	}
	
	protected void page(WebDriver driver, int liIndex) throws Exception {
		List<WebElement> pageableElements = driver.findElements(By.xpath("//ul[contains(@class,'pagination')]/li"));
		liIndex = liIndex % pageableElements.size();
		if (liIndex < 0) {
			liIndex += pageableElements.size();
		}
		pageableElements.get(liIndex).click();
		Thread.sleep(interval);
	}
	
	private List<WebElement> getTableList(WebDriver driver) {
		return driver.findElements(By.xpath("//div[@class='table-container']//table/tbody/tr"));
	}
	
	protected void clickActionButton(WebElement trElement, String buttonVariant) throws Exception {
		WebElement actionElement = trElement.findElement(By.xpath("//div[contains(@class,'action-container')]/a[contains(@class, '" + buttonVariant + "')]"));
		actionElement.click();
		Thread.sleep(interval);
	}
	
	protected void remove(WebElement trElement) throws Exception {
		clickActionButton(trElement, "btn-outline-danger");
		trElement.findElement(By.xpath("//div[@class='modal-content']//button[@class='btn btn-outline-danger btn-sm']")).click();
		Thread.sleep(interval);
	}
	
	protected void selectAllRows(WebDriver driver) {
		List<WebElement> trElements = driver.findElements(By.xpath("//div[@class='table-container']//table/tbody/tr"));
		if (CollectionUtils.isEmpty(trElements)) {
			return;
		}
		for (WebElement e : trElements) {
			new Actions(driver).click(e).perform();
		}
	}
	
}
