package net.saisimon.agtms.autotest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
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
		notificationManagement(driver);
		signout(driver);
	}
	
	protected void signout(WebDriver driver) throws Exception {
		// 用户栏目
		clickButton(driver, "user-nav-bar");
		// 登出
		clickButton(driver, "signout-btn");
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
		setNavigation(driver, 0, "random", "TEST");
		// 重置取消
		reset(driver, false);
		// 重置确认
		reset(driver, true);
		// 创建导航信息
		createNavigation(driver, 0, "random", "TEST-1");
		// 重复创建导航信息
		createNavigation(driver, 0, "random", "TEST-1");
		// 批量创建导航信息
		for (int i = 0; i <= 8; i++) {
			createNavigation(driver, 0, "random", "TEST-" + (i + 2));
		}
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 点击导航创建按钮
		clickButton(driver, "create-btn");
		createNavigation(driver, 1, "link", "TEST-SUB-1");
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
		select(driver, "//div[contains(@class,'name-operator-select')]", 0, false, 2);
		clickButton(driver, "search-btn");
		// 逗号分隔查询
		inputByClass(driver, "name-input", "TEST-1,TEST-2");
		select(driver, "//div[contains(@class,'name-operator-select')]", 0, false, 3);
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
		select(driver, "//div[@class='form-container']//div[contains(@class,'edit-field-select')]", 0, true, 3);
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
		// 空表单提交
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 设置模板
		setTemplate(driver, false, "TEST TEMPLATE", 1, 1, 1);
		// 重置确认
		reset(driver, true);
		// 创建模板
		createTemplate(driver, false, "TEST SIMPLE TEMPLATE", 1, 1, 1, 2, 3);
		// 重复表单提交
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 设置模板标题
		for (int i = 1; i <= 9; i++) {
			reset(driver, true);
			createTemplate(driver, true, "TEST SIMPLE TEMPLATE-" + i, 1, 1, 1, 2, 3);
		}
		// 重置确认
		reset(driver, true);
		// 创建模板
		createTemplate(driver, false, "TEST TEMPLATE", 4, 2, 1, 2, 3, 4, 5, 6, 7, 8);
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		// 精确查询
		inputByClass(driver, "title-input", "SIMPLE");
		clickButton(driver, "search-btn");
		// 模糊查询
		select(driver, "//div[contains(@class,'title-operator-select')]", 0, false, 2);
		clickButton(driver, "search-btn");
		// 点击清空按钮
		clickButton(driver, "clear-btn");
		// 点击刷新按钮
		clickButton(driver, "refresh-btn");
		// 下一页
		nextPage(driver);
		// 上一页
		prevPage(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		closeAlert(driver);
		// 编辑列表第一条
		clickActionButton(getTableList(driver).get(0), "btn-outline-primary");
		setTemplateTitle(driver, "TEST TEMPLATE CHANGED");
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 点击返回按钮
		clickButton(driver, "back-btn");
		// 查看列表第一条模板
		clickActionButton(getTableList(driver).get(0), "btn-outline-secondary");
		// 自定义对象管理
		management(driver);
		// 跳转模板管理列表页面
		to(driver, "/template/main");
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		clickButton(driver, "refresh-btn");
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		confireBatchRemove(driver);
	}
	
	protected void management(WebDriver driver) throws Exception {
		// 点击创建按钮
		clickButton(driver, "create-btn");
		// 空表单提交
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 创建
		inputById(driver, "column0field0-input", "TEST");
		inputById(driver, "column1field0-input", "-1");
		inputById(driver, "column2field0-input", "9.99");
		inputDateById(driver, "column3field0-input", 1);
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 重复提交
		clickButton(driver, "save-btn");
		closeAlert(driver);
		for (int i = 1; i <= 10; i++) {
			inputById(driver, "column0field0-input", "TEST-" + i);
			inputById(driver, "column1field0-input", i + "");
			inputById(driver, "column2field0-input", (i + 0.1) + "");
			inputDateById(driver, "column3field0-input", i + 1);
			clickButton(driver, "save-btn");
			closeAlert(driver);
		}
		// 返回
		clickButton(driver, "back-btn");
		// 编辑列表第一条
		clickActionButton(getTableList(driver).get(0), "btn-outline-primary");
		inputById(driver, "column0field0-input", "TEST-1");
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 编辑
		inputById(driver, "column0field0-input", "TEST-CHANGED");
		clickButton(driver, "save-btn");
		closeAlert(driver);
		// 返回
		clickButton(driver, "back-btn");
		// 导出
		export(driver, new int[] {1, 2, 3, 4}, 1);
		export(driver, new int[] {1, 2, 3, 4}, 2);
		export(driver, new int[] {1, 2, 3, 4}, 3);
		export(driver, new int[] {1, 2, 3, 4}, 4);
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		clickButton(driver, "refresh-btn");
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		confireBatchRemove(driver);
	}
	
	protected void export(WebDriver driver, int[] exportFieldSelectIndexs, int exportFileTypeSelectIndex) throws Exception {
		clickButton(driver, "export-btn");
		select(driver, "//div[contains(@class,'export-field-select')]", 0, true, exportFieldSelectIndexs);
		select(driver, "//div[contains(@class,'export-file-type-select')]", 0, false, exportFileTypeSelectIndex);
		clickButton(driver, "save-btn");
		closeAlert(driver);
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
		select(driver, "//div[contains(@class,'title-operator-select')]", 0, false, 2);
		clickButton(driver, "search-btn");
		// 逗号分隔查询
		inputByClass(driver, "title-input", "Whether,Test");
		select(driver, "//div[contains(@class,'title-operator-select')]", 0, false, 3);
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
		for (int i = 1; i <= 2; i++) {
			select(driver, "//div[@class='filter-container']//div[contains(@class,'taskType-select')]", 0, false, 1);
			clickButton(driver, "search-btn");
		}
		// 清空
		clickButton(driver, "clear-btn");
		for (int i = 1; i <= 7; i++) {
			// 选择操作类型
			select(driver, "//div[@class='filter-container']//div[contains(@class,'handleStatus-select')]", 0, false, i);
			// 搜索
			clickButton(driver, "search-btn");
		}
		// 清空
		clickButton(driver, "clear-btn");
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		confireBatchRemove(driver);
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
			select(driver, "//div[@class='filter-container']//div[contains(@class,'operateType-select')]", 0, false, i);
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
	
	protected void notificationManagement(WebDriver driver) throws Exception {
		// 跳转模板管理列表页面
		to(driver, "/notification/main");
		// 显示筛选条件
		if (!findElement(driver, By.id("filter-toggle")).isDisplayed()) {
			clickButton(driver, "filter-switch-btn");
		}
		for (int i = 1; i <= 2; i++) {
			// 选择通知类型
			select(driver, "//div[@class='filter-container']//div[contains(@class,'type-select')]", 0, false, i);
			// 搜索
			clickButton(driver, "search-btn");
		}
		// 清空
		clickButton(driver, "clear-btn");
		for (int i = 1; i <= 2; i++) {
			// 选择通知状态
			select(driver, "//div[@class='filter-container']//div[contains(@class,'status-select')]", 0, false, i);
			// 搜索
			clickButton(driver, "search-btn");
		}
		// 清空
		clickButton(driver, "clear-btn");
		// 删除列表第一条
		remove(getTableList(driver).get(0));
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量已读按钮
		clickButton(driver, "batch-read-btn");
		// 勾选列表所有行
		selectAllRows(driver);
		// 点击批量删除按钮
		clickButton(driver, "batch-remove-btn");
		// 确认删除
		confireBatchRemove(driver);
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
		findElement(driver, By.xpath("//div[@class='modal-content']//button[@class='btn save-btn btn-outline-primary btn-sm']")).click();
		Thread.sleep(interval);
	}
	
	protected void createOptionSelection(WebDriver driver, String title, LinkedHashMap<String, String> optionMap) throws Exception {
		setOptionSelection(driver, title, optionMap);
		clickButton(driver, "save-btn");
		closeAlert(driver);
	}
	
	protected void setNavigation(WebDriver driver, int navigationIndex, String icon, String title) throws Exception {
		if (navigationIndex > 0) {
			select(driver, "//div[@class='edit-container']//div[@class='form-container']//div[contains(@class,'vue-treeselect')]", 0, false, navigationIndex);
		}
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
	
	protected void setTemplateTitle(WebDriver driver, String title) throws Exception {
		clickButton(driver, "title-editor");
		findElement(driver, By.className("title-input")).clear();
		clickButton(driver, "title-editor");
		findElement(driver, By.className("title-input")).sendKeys(title);
	}
	
	protected void setTemplate(WebDriver driver, boolean quick, String title, int columnIndex, int sourceIndex, int... functionIndexes) throws Exception {
		// 标题
		clickButton(driver, "title-editor");
		inputByClass(driver, "title-input", title);
		// 新增列
		for (int i = 1; i < columnIndex; i++) {
			clickButton(driver, "add-column-btn");
		}
		// 设置列名
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "input-editor", i + 1);
			inputByClass(driver, "column" + i + "-input", "TEST-" + (i + 1));
		}
		// 设置字段名
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "fieldName-editor", i);
			inputByClass(driver, "fieldName-field0-input", "TEST-FIELD-" + (i + 1));
		}
		if (quick) {
			return;
		}
		// 设置字段类型
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "fieldType-editor", i);
			int index = 0;
			if (i != 0) {
				index = 1;
			}
			select(driver, "//div[@class='template-container']//div[contains(@class,'fieldType-field0-select')]", index, false, (i % 4) + 1);
		}
		// 设置筛选
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "filter-editor", i);
			int index = 0;
			if (i != 0) {
				index = 1;
			}
			select(driver, "//div[@class='template-container']//div[contains(@class,'filter-field0-select')]", index, false, 2);
		}
		// 设置排序
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "sorted-editor", i);
			int index = 0;
			if (i != 0) {
				index = 1;
			}
			select(driver, "//div[@class='template-container']//div[contains(@class,'sorted-field0-select')]", index, false, 2);
		}
		// 设置必填
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "required-editor", i);
			int index = 0;
			if (i != 0) {
				index = 1;
			}
			select(driver, "//div[@class='template-container']//div[contains(@class,'required-field0-select')]", index, false, 2);
		}
		// 设置唯一
		for (int i = 0; i < columnIndex; i++) {
			clickButton(driver, "uniqued-editor", i);
			int index = 0;
			if (i != 0) {
				index = 1;
			}
			select(driver, "//div[@class='template-container']//div[contains(@class,'uniqued-field0-select')]", index, false, 2);
		}
		// 设置功能
		select(driver, "//div[@class='template-container']//div[@id='functions-input']", 0, true, functionIndexes);
		// 设置数据来源
		select(driver, "//div[@class='template-container']//div[@id='source-input']", 0, false, sourceIndex);
	}
	
	protected void createTemplate(WebDriver driver, boolean quick, String title, int columnIndex, int sourceIndex, int... functionIndexes) throws Exception {
		setTemplate(driver, quick, title, columnIndex, sourceIndex, functionIndexes);
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
	
	protected void clickButtonById(WebDriver driver, String idName) throws Exception {
		WebElement buttonElement = findElement(driver, By.id(idName));
		if (buttonElement == null) {
			return;
		}
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].click();", buttonElement);
		Thread.sleep(interval);
	}
	
	protected void clickButton(WebDriver driver, String className) throws Exception {
		clickButton(driver, className, 0);
	}
	
	protected void clickButton(WebDriver driver, String className, int index) throws Exception {
		WebElement buttonElement = findElement(driver, By.className(className), index);
		if (buttonElement == null) {
			return;
		}
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].click();", buttonElement);
		Thread.sleep(interval);
	}
	
	protected void select(WebDriver driver, String selectPath, int index, boolean multiple, int... selectIndexs) throws Exception {
		WebElement selectElement = findElement(driver, By.xpath(selectPath + "//div[@class='vue-treeselect__control']"), index);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView()", selectElement);
		selectElement.click();
		if (selectIndexs != null) {
			for (int selectIndex : selectIndexs) {
				findElement(driver, By.xpath(selectPath + "//div[@class='vue-treeselect__menu-container']//div[contains(@class,'vue-treeselect__list-item')]["+ selectIndex +"]")).click();
			}
		}
		if (multiple) {
			findElement(driver, By.xpath(selectPath + "//div[@class='vue-treeselect__control-arrow-container']")).click();
		}
		Thread.sleep(interval);
	}
	
	protected void inputDateById(WebDriver driver, String idName, int index) throws Exception {
		clickButtonById(driver, idName);
		WebElement element = findElement(driver, By.xpath("//div[@class='vdp-datepicker__calendar']//span[@class='cell day'][" + index + "]"));
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView()", element);
		element.click();
		Thread.sleep(interval);
	}
	
	protected void inputById(WebDriver driver, String idName, String text) throws Exception {
		WebElement element = findElement(driver, By.id(idName));
		element.clear();
		element.sendKeys(text);
	}
	
	protected void inputByClass(WebDriver driver, String className, String text) throws Exception {
		try {
			WebElement element = findElement(driver, By.className(className));
			element.clear();
			element.sendKeys(text);
		} catch (StaleElementReferenceException e) {
			WebElement element = findElement(driver, By.className(className));
			element.clear();
			element.sendKeys(text);
		}
	}
	
	protected WebElement findElement(WebDriver driver, By by) {
		return findElement(driver, by, 0);
	}
	
	protected WebElement findElement(WebDriver driver, By by, int index) {
		try {
			(new WebDriverWait(driver, timeout / 1000)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					List<WebElement> elements = driver.findElements(by);
					if (elements.size() == 0) {
						return false;
					}
					WebElement element = elements.get(index);
					return element.isEnabled();
				}
			});
		} catch (TimeoutException e) {
			log.warn("find Element Timeout: " + by);
			return null;
		}
		return driver.findElements(by).get(index);
	}
	
	protected void prevPage(WebDriver driver) throws Exception {
		List<WebElement> pageableElements = driver.findElements(By.xpath("//div[@id='pagination']/div[@class='row']/div"));
		if (CollectionUtils.isEmpty(pageableElements)) {
			return;
		}
		WebElement prevElement = pageableElements.get(0);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView()", prevElement);
		prevElement.click();
		Thread.sleep(interval);
	}
	
	protected void nextPage(WebDriver driver) throws Exception {
		List<WebElement> pageableElements = driver.findElements(By.xpath("//div[@id='pagination']/div[@class='row']/div"));
		if (CollectionUtils.isEmpty(pageableElements)) {
			return;
		}
		WebElement nextElement = pageableElements.get(2);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView()", nextElement);
		nextElement.click();
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
		List<WebElement> checkboxElements = driver.findElements(By.xpath("//div[@class='table-container']//table/thead/tr/th/input"));
		if (CollectionUtils.isEmpty(checkboxElements)) {
			return;
		}
		new Actions(driver).click(checkboxElements.get(0)).perform();
	}
	
}
