package net.saisimon.agtms.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.main.banner-mode=OFF", "logging.level.net.saisimon=DEBUG", "eureka.client.enabled=false"})
@AutoConfigureMockMvc
public class MainControllerTest extends AbstractControllerTest {

	@Value("${extra.admin.username:admin}")
	private String username;
	@Value("${extra.admin.password:123456}")
	private String password;
	@Value("${extra.default.password:123456}")
	private String defaultPassword;

	/* UserMainController Start */
	@Test
	public void testUserMainGrid() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/user/main/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/user/main/grid", null, adminToken);
	}

	@Test
	public void testUserMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/user/main/list", param, body, adminToken);
	}

	@Test
	public void testUserMainLockAndUnLock() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("id", "0");
		sendPost("/user/main/lock", param, adminToken);

		UserToken testToken = login("test", "test");
		Long testUserId = testToken.getUserId();
		param.put("id", testUserId.toString());
		sendPost("/user/main/lock", param, adminToken);

		testToken = login("test", "test");

		param.put("id", "0");
		sendPost("/user/main/unlock", param, adminToken);

		param.put("id", testUserId.toString());
		sendPost("/user/main/unlock", param, adminToken);
	}

	@Test
	public void testUserMainResetPassword() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("id", "0");
		sendPost("/user/main/reset/password", param, adminToken);

		UserToken testToken = login("test", "test");
		Long testUserId = testToken.getUserId();
		param.put("id", testUserId.toString());
		sendPost("/user/main/reset/password", param, adminToken);

		testToken = login("test", defaultPassword);

		UserPasswordChangeParam userPasswordChangeParam = new UserPasswordChangeParam();
		userPasswordChangeParam.setNewPassword("test");
		userPasswordChangeParam.setOldPassword(defaultPassword);
		sendPost("/user/password/change", userPasswordChangeParam, testToken);

		testToken = login("test", "test");
	}
	/* UserMainController End */

	/* NavigationMainController Start */
	@Test
	public void testNavigationMainGrid() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/navigation/main/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/navigation/main/grid", null, adminToken);
	}

	@Test
	public void testNavigationMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/navigation/main/list", param, body, adminToken);

		UserToken testToken = login("test", "test");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/navigation/main/list", param, body, testToken);
	}

	@Test
	public void testNavigationMainSide() throws Exception {
		UserToken adminToken = login(username, password);
		sendPost("/navigation/main/side", null, null, adminToken);

		UserToken testToken = login("test", "test");
		sendPost("/navigation/main/side", null, null, testToken);
	}

	@Test
	public void testNavigationMainBatchGrid() throws Exception {
		UserToken adminToken = login(username, password);
		sendPost("/navigation/main/batch/grid", null, null, adminToken);

		UserToken testToken = login("test", "test");
		sendPost("/navigation/main/batch/grid", null, null, testToken);
	}
	/* NavigationMainController End */

	/* TemplateMainController Start */
	@Test
	public void testTemplateMainGrid() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/template/main/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/template/main/grid", null, adminToken);
	}

	@Test
	public void testTemplateMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/template/main/list", param, body, adminToken);

		UserToken testToken = login("test", "test");
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
		UserToken testToken = login("test", "test");
		sendPost("/selection/main/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/selection/main/grid", null, adminToken);
	}

	@Test
	public void testSelectionMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/selection/main/list", param, body, adminToken);

		UserToken testToken = login("test", "test");
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
		UserToken testToken = login("test", "test");
		sendPost("/task/main/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/task/main/grid", null, adminToken);
	}

	@Test
	public void testTaskMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/task/main/list", param, body, adminToken);

		UserToken testToken = login("test", "test");
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
		UserToken testToken = login("test", "test");
		sendPost("/operation/main/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/operation/main/grid", null, adminToken);
	}

	@Test
	public void testOperationMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/operation/main/list", param, body, adminToken);

		UserToken testToken = login("test", "test");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/operation/main/list", param, body, testToken);
	}
	/* OperationMainController End */

	/* ManagementMainController Start */
	@Test
	public void testManagementMainGrid() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/management/main/1/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/management/main/1/grid", null, adminToken);
	}

	@Test
	public void testManagementMainList() throws Exception {
		UserToken adminToken = login(username, password);
		Map<String, String> param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		Map<String, Object> body = new HashMap<>();
		sendPost("/management/main/1/list", param, body, adminToken);

		UserToken testToken = login("test", "test");
		param = new HashMap<>();
		param.put("index", "0");
		param.put("size", "10");
		body = new HashMap<>();
		sendPost("/management/main/1/list", param, body, testToken);
	}

	@Test
	public void testManagementMainBatchGrid() throws Exception {
		UserToken testToken = login("test", "test");
		sendPost("/management/main/1//batch/grid", null, testToken);

		UserToken adminToken = login(username, password);
		sendPost("/management/main/1//batch/grid", null, adminToken);
	}
	/* ManagementMainController Start */

}