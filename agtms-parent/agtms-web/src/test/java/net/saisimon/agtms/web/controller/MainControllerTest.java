package net.saisimon.agtms.web.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.OperationServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.web.config.runner.InitRunner;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainControllerTest.TestMain.class, properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR"})
@AutoConfigureMockMvc
public class MainControllerTest extends AbstractControllerTest {
	
	@Autowired
	private AgtmsProperties agtmsProperties;
	@Autowired
	private InitRunner initRunner;
	
	@Before
	public void setUp() throws Exception {
		initRunner.run(new String[0]);
	}
	
	@After
	public void tearsDown() throws Exception {
		FilterRequest filter = FilterRequest.build();
		UserServiceFactory.get().delete(filter);
		UserRoleServiceFactory.get().delete(filter);
		TemplateServiceFactory.get().delete(filter);
		TaskServiceFactory.get().delete(filter);
		SelectionServiceFactory.get().delete(filter);
		RoleServiceFactory.get().delete(filter);
		RoleResourceServiceFactory.get().delete(filter);
		ResourceServiceFactory.get().delete(filter);
		OperationServiceFactory.get().delete(filter);
	}
	
	/* UserMainController Start */
	@Test
	public void testUserMainGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/user/main/grid", null, testToken, status().isForbidden());
		
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/user/main/grid", null, adminToken);
	}
	
	@Test
	public void testUserMainList() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		param.put("sort", "");
		Map<String, Object> body = new HashMap<>();
		sendPost("/user/main/list", param, body, adminToken);
		
		body = new HashMap<>();
		List<Map<String, Object>> andFilters = new ArrayList<>();
		Map<String, Object> andFilter = new HashMap<>();
		andFilter.put("key", "loginName");
		andFilter.put("operator", "$regex");
		andFilter.put("type", "string");
		andFilter.put("value", "admin");
		andFilters.add(andFilter);
		body.put("andFilters", andFilters);
		sendPost("/user/main/list", param, body, adminToken);
		
		body = new HashMap<>();
		andFilters = new ArrayList<>();
		andFilter = new HashMap<>();
		andFilter.put("key", "loginName");
		andFilter.put("operator", "$eq");
		andFilter.put("type", "string");
		andFilter.put("value", "admin");
		andFilters.add(andFilter);
		body.put("andFilters", andFilters);
		sendPost("/user/main/list", param, body, adminToken);
		
		body = new HashMap<>();
		andFilters = new ArrayList<>();
		andFilter = new HashMap<>();
		andFilter.put("key", "loginName");
		andFilter.put("operator", "$in");
		andFilter.put("type", "string");
		andFilter.put("value", Arrays.asList("admin", "editor"));
		andFilters.add(andFilter);
		body.put("andFilters", andFilters);
		sendPost("/user/main/list", param, body, adminToken);
		
		body = new HashMap<>();
		andFilters = new ArrayList<>();
		andFilter = new HashMap<>();
		andFilter.put("key", "loginName");
		andFilter.put("operator", "$in");
		andFilter.put("type", "string");
		andFilter.put("value", Arrays.asList("admin", "editor"));
		andFilters.add(andFilter);
		body.put("andFilters", andFilters);
		sendPost("/user/main/list", param, body, adminToken);
		
		body = new HashMap<>();
		andFilters = new ArrayList<>();
		Map<String, Object> gte = new HashMap<>();
		gte.put("key", "createTime");
		gte.put("operator", "$gte");
		gte.put("type", "date");
		gte.put("value", "2019-04-01T00:00:00.000Z");
		andFilters.add(gte);
		Map<String, Object> lte = new HashMap<>();
		lte.put("key", "createTime");
		lte.put("operator", "$lte");
		lte.put("type", "date");
		lte.put("value", "2019-04-30T00:00:00.000Z");
		andFilters.add(lte);
		body.put("andFilters", andFilters);
		sendPost("/user/main/list", param, body, adminToken);
		
		sendPost("/user/main/grid", null, adminToken);
	}
	
	@Test
	public void testUserMainLockAndUnLock() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("id", "0");
		sendPost("/user/main/lock", param, adminToken, ErrorMessage.User.ACCOUNT_NOT_EXIST.getCode());
		
		UserToken testToken = login("editor", "editor");
		Long testUserId = testToken.getUserId();
		param.put("id", testUserId.toString());
		sendPost("/user/main/lock", param, adminToken);
		
		UserAuthParam userAuthParam = new UserAuthParam();
		userAuthParam.setName("editor");
		userAuthParam.setPassword("editor");
		sendPost("/user/auth", userAuthParam, null, ErrorMessage.User.ACCOUNT_LOCKED.getCode());
		
		param.put("id", "0");
		sendPost("/user/main/unlock", param, adminToken, ErrorMessage.User.ACCOUNT_NOT_EXIST.getCode());
		
		param.put("id", testUserId.toString());
		sendPost("/user/main/unlock", param, adminToken);
	}
	
	@Test
	public void testUserMainResetPassword() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("id", "0");
		sendPost("/user/main/reset/password", param, adminToken, ErrorMessage.User.ACCOUNT_NOT_EXIST.getCode());
		
		UserToken testToken = login("editor", "editor");
		Long testUserId = testToken.getUserId();
		param.put("id", testUserId.toString());
		sendPost("/user/main/reset/password", param, adminToken);
		
		testToken = login("editor", agtmsProperties.getResetPassword());
		
		UserPasswordChangeParam userPasswordChangeParam = new UserPasswordChangeParam();
		userPasswordChangeParam.setNewPassword("editor");
		userPasswordChangeParam.setOldPassword(agtmsProperties.getResetPassword());
		sendPost("/user/password/change", userPasswordChangeParam, testToken);
		
		testToken = login("editor", "editor");
	}
	/* UserMainController End */
	
	/* NavigationMainController Start */
	@Test
	public void testNavigationMainGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/navigation/main/grid", null, testToken);
		
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/navigation/main/grid", null, adminToken);
	}
	
	@Test
	public void testNavigationMainList() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/navigation/main/list", param, body, adminToken);
		
		UserToken testToken = login("editor", "editor");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/navigation/main/list", param, body, testToken);
	}
	
	@Test
	public void testNavigationMainBatchGrid() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/navigation/main/batch/grid", null, null, adminToken);
		
		UserToken testToken = login("editor", "editor");
		sendPost("/navigation/main/batch/grid", null, null, testToken);
	}
	/* NavigationMainController End */
	
	/* TemplateMainController Start */
	@Test
	public void testTemplateMainGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/template/main/grid", null, testToken);
		
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/template/main/grid", null, adminToken);
	}
	
	@Test
	public void testTemplateMainList() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/template/main/list", param, body, adminToken);
		
		UserToken testToken = login("editor", "editor");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/template/main/list", param, body, testToken);
	}
	/* TemplateMainController End */
	
	/* SelectionMainController Start */
	@Test
	public void testSelectionMainGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/selection/main/grid", null, testToken);
		
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/selection/main/grid", null, adminToken);
	}
	
	@Test
	public void testSelectionMainList() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/selection/main/list", param, body, adminToken);
		
		UserToken testToken = login("editor", "editor");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/selection/main/list", param, body, testToken);
	}
	/* SelectionMainController End */
	
	/* TaskMainController Start */
	@Test
	public void testTaskMainGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/task/main/grid", null, testToken);
		
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/task/main/grid", null, adminToken);
	}
	
	@Test
	public void testTaskMainList() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/task/main/list", param, body, adminToken);
		
		UserToken testToken = login("editor", "editor");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/task/main/list", param, body, testToken);
	}
	/* TaskMainController End */
	
	/* OperationMainController Start */
	@Test
	public void testOperationMainGrid() throws Exception {
		UserToken testToken = login("editor", "editor");
		sendPost("/operation/main/grid", null, testToken);
		
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		sendPost("/operation/main/grid", null, adminToken);
	}
	
	@Test
	public void testOperationMainList() throws Exception {
		UserToken adminToken = login(agtmsProperties.getAdminUsername(), agtmsProperties.getAdminPassword());
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/operation/main/list", param, body, adminToken);
		
		UserToken testToken = login("editor", "editor");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/operation/main/list", param, body, testToken);
	}
	/* OperationMainController End */
	
	@SpringBootApplication
	@ComponentScan(basePackages="net.saisimon.agtms")
	public static class TestMain {}
	
}
