package net.saisimon.agtms.autotest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	
	private static final String TEST_USERNAME = "editor";
	private static final String TEST_PASSWORD = "editor";

	@Override
	public void detroyDriver(WebDriver driver) {
		log.debug("Detroy Driver");
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
		signin(driver);
		navigationManagement(driver);
		templateManagement(driver);
		selectionManagement(driver);
		taskManagement(driver);
		operationManagement(driver);
		signout(driver);
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
		inputById(driver, "username", TEST_USERNAME);
		// 输入密码
		inputById(driver, "password", TEST_PASSWORD);
		// 点击登录按钮
		clickButton(driver, "signin-btn");
	}

	protected void navigationManagement(WebDriver driver) throws Exception {
		// 跳转导航管理列表页面
		to(driver, "/navigation/main");
		// 点击导航创建按钮
		clickButton(driver, "create-btn");
		// 空表单登录提交
		clickButton(driver, "save-btn");
		// 设置导航信息
		setNavigation(driver, 1, "random", "TEST");
		// 重置取消
		reset(driver, false);
		// 重置确认
		reset(driver, true);
		// 创建导航信息
		createNavigation(driver, 1, "random", "TEST-1");
		// 重复创建导航信息
		createNavigation(driver, 1, "random", "TEST-1");
		// 批量创建导航信息
		for (int i = 0; i < 9; i++) {
			createNavigation(driver, 1, "random", "TEST-" + (i + 2));
		}
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 点击导航创建按钮
		clickButton(driver, "create-btn");
		createNavigation(driver, 2, "link", "TEST-SUB-1");
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// 精确查询
		inputByClass(driver, "name-input", "TEST-1");
		clickButton(driver, "search-btn");
		// 模糊查询
		select(driver, "//div[contains(@class,'name-operator-select')]", 2);
		clickButton(driver, "search-btn");
		// 逗号分隔查询
		inputByClass(driver, "name-input", "TEST-1,TEST-2");
		select(driver, "//div[contains(@class,'name-operator-select')]", 3);
		clickButton(driver, "search-btn");
		// 点击清空按钮
		clickButton(driver, "clear-btn");
		// 点击刷新按钮
		clickButton(driver, "refresh-btn");
		// 下一页
		nextPage(driver);
		// 上一页
		prevPage(driver);
		// 点击批量编辑按钮
		clickButton(driver, "batch-edit-btn");
		closeAlert(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		closeAlert(driver);
		// 编辑列表第一条
		clickActionButton(getTableList(driver).get(0), "btn-outline-primary");
		// 设置导航信息
		setNavigation(driver, 2, "bars", "TEST");
		// 重置确认
		reset(driver, true);
		// 创建导航信息
		createNavigation(driver, 2, "bars", "TEST-CHANGE");
		// 返回
		clickButton(driver, "back-btn");
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量编辑按钮
		clickButton(driver, "batch-edit-btn");
		// 选择图标
		select(driver, "//div[@class='batch-edit-container']//div[@class='form-container']//div[@class='multiselect']", 3);
		inputById(driver, "icon-input", "list");
		// 批量保存
		clickButton(driver, "save-btn");
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		confireBatchRemove(driver);
	}

	protected void templateManagement(WebDriver driver) throws Exception {
		// 跳转模板管理列表页面
		to(driver, "/template/main");
		// 点击模板创建按钮
		clickButton(driver, "create-btn");
		// 空表单登录提交
		clickButton(driver, "save-btn");
		// 关闭提示窗
		closeAlert(driver);
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// TODO
		
	}
	
	protected void selectionManagement(WebDriver driver) throws Exception {
		// 跳转下拉列表管理列表页面
		to(driver, "/selection/main");
		// 点击下拉列表创建按钮
		clickButton(driver, "create-btn");
		// 空表单登录提交
		clickButton(driver, "save-btn");
		// 关闭提示窗
		closeAlert(driver);
		LinkedHashMap<String, String> optionMap = new LinkedHashMap<>();
		optionMap.put("0", "Woman");
		optionMap.put("1", "Man");
		// 设置下拉列表信息
		setOptionSelection(driver, "Gender", optionMap);
		// 重置取消
		reset(driver, false);
		// 重置确认
		reset(driver, true);
		// 创建下拉列表信息
		createOptionSelection(driver, "Gender", optionMap);
		// 重置确认
		reset(driver, true);
		// 重复创建下拉列表信息
		createOptionSelection(driver, "Gender", optionMap);
		// 重置确认
		reset(driver, true);
		LinkedHashMap<String, String> whetherMap = new LinkedHashMap<>();
		whetherMap.put("0", "No");
		whetherMap.put("1", "Yes");
		createOptionSelection(driver, "Whether", whetherMap);
		// 重置确认
		reset(driver, true);
		LinkedHashMap<String, String> testMap = new LinkedHashMap<>();
		testMap.put("0", "Zero");
		testMap.put("1", "One");
		testMap.put("2", "Two");
		testMap.put("3", "Three");
		testMap.put("4", "Four");
		testMap.put("5", "Five");
		createOptionSelection(driver, "Test", testMap);
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// 精确查询
		inputByClass(driver, "title-input", "Gender");
		clickButton(driver, "search-btn");
		// 模糊查询
		inputByClass(driver, "title-input", "er");
		select(driver, "//div[contains(@class,'title-operator-select')]", 2);
		clickButton(driver, "search-btn");
		// 逗号分隔查询
		inputByClass(driver, "title-input", "Whether,Test");
		select(driver, "//div[contains(@class,'title-operator-select')]", 3);
		clickButton(driver, "search-btn");
		// 点击清空按钮
		clickButton(driver, "clear-btn");
		// 点击刷新按钮
		clickButton(driver, "refresh-btn");
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		confireBatchRemove(driver);
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
		for (int i = 1; i <= 10; i++) {
			// 选择操作类型
			select(driver, "//div[@class='filter-container']//div[contains(@class,'operateType-select')]", i);
			// 搜索
			clickButton(driver, "search-btn");
		}
		// 清空
		clickButton(driver, "clear-btn");
		// 下一页
		nextPage(driver);
		// 上一页
		prevPage(driver);
	}
	
	protected void setOptionSelection(WebDriver driver, String title, LinkedHashMap<String, String> optionMap) throws Exception {
		inputById(driver, "title-input", title);
		int idx = 0;
		for (Map.Entry<String, String> entry : optionMap.entrySet()) {
			if (idx != 0) {
				clickButton(driver, "add-option-div");
			}
			inputById(driver, "optionValue-" + idx + "-input", entry.getKey());
			inputById(driver, "optionText-" + idx + "-input", entry.getValue());
			idx++;
		}
	}
	
	protected void confireBatchRemove(WebDriver driver) throws Exception {
		findElement(driver, By.xpath("//div[@class='batch-remove-container']//div[@class='modal-content']//button[@class='btn btn-outline-danger btn-sm']")).click();
		Thread.sleep(interval);
	}
	
	protected void createOptionSelection(WebDriver driver, String title, LinkedHashMap<String, String> optionMap) throws Exception {
		setOptionSelection(driver, title, optionMap);
		clickButton(driver, "save-btn");
		closeAlert(driver);
	}
	
	protected void setNavigation(WebDriver driver, int navigationIndex, String icon, String title) throws Exception {
		select(driver, "//div[@class='edit-container']//div[@class='form-container']//div[contains(@class,'multiselect')]", navigationIndex);
		inputById(driver, "icon-input", icon);
		inputById(driver, "name-input", title);
	}
	
	protected void to(WebDriver driver, String path) throws Exception {
		driver.navigate().to(URLUtil.formatUrl(url + path));
		Thread.sleep(interval);
	}
	
	protected void createNavigation(WebDriver driver, int navigationIndex, String icon, String title) throws Exception {
		setNavigation(driver, navigationIndex, icon, title);
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
	
	protected void prevPage(WebDriver driver) throws Exception {
		List<WebElement> pageableElements = driver.findElements(By.xpath("//div[@id='pagination']/div[@class='row']/div"));
		if (CollectionUtils.isEmpty(pageableElements)) {
			return;
		}
		pageableElements.get(0).click();
		Thread.sleep(interval);
	}
	
	protected void nextPage(WebDriver driver) throws Exception {
		List<WebElement> pageableElements = driver.findElements(By.xpath("//div[@id='pagination']/div[@class='row']/div"));
		if (CollectionUtils.isEmpty(pageableElements)) {
			return;
		}
		pageableElements.get(2).click();
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
		List<WebElement> trElements = driver.findElements(By.xpath("//div[@class='table-container']//table/tbody/tr/th/input"));
		if (CollectionUtils.isEmpty(trElements)) {
			return;
		}
		for (WebElement e : trElements) {
			new Actions(driver).click(e).perform();
		}
	}
	
}
