package net.saisimon.agtms.web.controller;

import java.util.HashMap;
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
import net.saisimon.agtms.core.property.AccountProperties;
import net.saisimon.agtms.web.config.runner.InitRunner;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;
import net.saisimon.agtms.web.dto.req.UserProfileSaveParam;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserControllerTest.TestMain.class, properties = {"spring.main.bannerMode=OFF", "logging.level.root=ERROR"})
@AutoConfigureMockMvc
public class UserControllerTest extends AbstractControllerTest {
	
	@Autowired
	private AccountProperties accountProperties;
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
	
	@Test
	public void testAuth() throws Exception {
		UserAuthParam userAuthParam = new UserAuthParam();
		userAuthParam.setName("editor");
		sendPost("/user/auth", userAuthParam, null, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		userAuthParam = new UserAuthParam();
		userAuthParam.setName("editor");
		userAuthParam.setPassword("123456");
		sendPost("/user/auth", userAuthParam, null, ErrorMessage.User.USERNAME_OR_PASSWORD_NOT_CORRECT.getCode());
		
		UserToken userToken = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		
		Map<String, String> param = new HashMap<>();
		param.put("uid", "a");
		param.put("token", "token");
		sendPost("/api/check/token", param, null, -1);
		
		param = new HashMap<>();
		param.put("uid", "100");
		param.put("token", "token");
		sendPost("/api/check/token", param, null, -1);
		
		param = new HashMap<>();
		param.put("uid", "2");
		param.put("token", userToken.getToken());
		sendPost("/api/check/token", param, null, -1);
	}
	
	@Test
	public void testNav() throws Exception {
		UserToken adminToken = login(accountProperties.getAdmin().getUsername(), accountProperties.getAdmin().getPassword());
		sendPost("/user/nav", null, null, adminToken);
		
		UserToken testToken = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		sendPost("/user/nav", null, null, testToken);
	}
	
	@Test
	public void testNotification() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		sendPost("/user/notification", null, null, token);
	}
	
	@Test
	public void testNotificationRead() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		Map<String, String> param = new HashMap<>();
		param.put("id", "-1");
		sendPost("/user/notification/read", param, token, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		param = new HashMap<>();
		param.put("id", "1000");
		sendPost("/user/notification/read", param, token, ErrorMessage.Notification.NOTIFICATION_NOT_EXIST.getCode());
	}
	
	@Test
	public void testNotifications() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		sendPost("/user/notifications", null, null, token);
	}
	
	@Test
	public void testPasswordChange() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		UserPasswordChangeParam param = new UserPasswordChangeParam();
		sendPost("/user/password/change", param, token, ErrorMessage.Common.MISSING_REQUIRED_FIELD.getCode());
		
		param.setNewPassword(buildString(17));
		param.setOldPassword("a");
		sendPost("/user/password/change", param, token, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		param.setNewPassword("a");
		param.setOldPassword(buildString(17));
		sendPost("/user/password/change", param, token, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		param.setNewPassword("123456");
		param.setOldPassword("123456");
		sendPost("/user/password/change", param, token, ErrorMessage.User.OLD_PASSWORD_NOT_CORRECT.getCode());
		
		param.setNewPassword("123456");
		param.setOldPassword("editor");
		sendPost("/user/password/change", param, token);
		
		token = login("editor", "123456");
		
		param.setOldPassword("123456");
		param.setNewPassword("editor");
		sendPost("/user/password/change", param, token);
	}
	
	@Test
	public void testProfileInfo() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		sendPost("/user/profile/info", null, token);
	}
	
	@Test
	public void testProfileSave() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		UserProfileSaveParam param = new UserProfileSaveParam();
		sendPost("/user/profile/save", param, token);
		
		param.setAvatar(null);
		param.setRemark(null);
		param.setNickname(buildString(33));
		sendPost("/user/profile/save", param, token, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		param.setAvatar(null);
		param.setRemark(buildString(513));
		param.setNickname(null);
		sendPost("/user/profile/save", param, token, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		param.setAvatar(buildString(65));
		param.setRemark(null);
		param.setNickname(null);
		sendPost("/user/profile/save", param, token, ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.getCode());
		
		param.setAvatar("");
		param.setRemark("Editor");
		param.setNickname("Editor");
		sendPost("/user/profile/save", param, token);
	}
	
	@Test
	public void testLogout() throws Exception {
		UserToken token = login(accountProperties.getEditor().getUsername(), accountProperties.getEditor().getPassword());
		logout(token);
	}
	
	@SpringBootApplication
	@ComponentScan(basePackages="net.saisimon.agtms")
	public static class TestMain {}
	
}
