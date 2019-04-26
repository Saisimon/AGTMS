package net.saisimon.agtms.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;
import net.saisimon.agtms.web.dto.req.UserProfileSaveParam;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.main.banner-mode=OFF", "logging.level.net.saisimon=DEBUG", "eureka.client.enabled=false"})
@AutoConfigureMockMvc
public class UserControllerTest extends AbstractControllerTest {
	
	@Test
	public void testAuth() throws Exception {
		login("test", null);
		login("test", "123456");
		login("test", "test");
	}
	
	@Test
	public void testPasswordChange() throws Exception {
		UserToken token = login("test", "test");
		UserPasswordChangeParam param = new UserPasswordChangeParam();
		sendPost("/user/password/change", param, token);
		
		param.setNewPassword(buildString(17));
		param.setOldPassword("a");
		sendPost("/user/password/change", param, token);
		
		param.setNewPassword("a");
		param.setOldPassword(buildString(17));
		sendPost("/user/password/change", param, token);
		
		param.setNewPassword("123456");
		param.setOldPassword("123456");
		sendPost("/user/password/change", param, token);
		
		param.setNewPassword("123456");
		param.setOldPassword("test");
		sendPost("/user/password/change", param, token);
		
		param.setOldPassword("123456");
		param.setNewPassword("test");
		sendPost("/user/password/change", param, token);
	}
	
	@Test
	public void testProfileInfo() throws Exception {
		UserToken token = login("test", "test");
		sendPost("/user/profile/info", null, token);
	}
	
	@Test
	public void testProfileSave() throws Exception {
		UserToken token = login("test", "test");
		UserProfileSaveParam param = new UserProfileSaveParam();
		sendPost("/user/profile/save", param, token);
		
		param.setAvatar(null);
		param.setRemark(null);
		param.setNickname(buildString(33));
		sendPost("/user/profile/save", param, token);
		
		param.setAvatar(null);
		param.setRemark(buildString(513));
		param.setNickname(null);
		sendPost("/user/profile/save", param, token);
		
		param.setAvatar(buildString(65));
		param.setRemark(null);
		param.setNickname(null);
		sendPost("/user/profile/save", param, token);
		
		param.setAvatar("");
		param.setRemark("Test");
		param.setNickname("Test");
		sendPost("/user/profile/save", param, token);
	}
	
	@Test
	public void testLogout() throws Exception {
		logout(null);
		UserToken token = login("test", "test");
		logout(token);
	}
	
}
