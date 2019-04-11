package net.saisimon.agtms.web.config.runner;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;

/**
 * 初始化操作
 * 
 * @author saisimon
 *
 */
public class InitRunner implements CommandLineRunner {
	
	@Value("${extra.admin.username:admin}")
	private String username;
	@Value("${extra.admin.password:123456}")
	private String password;
	
	private static final String TEST_USERNAME = "test";
	private static final String TEST_PASSWORD = "test";

	@Override
	@Transactional(rollbackOn=Exception.class)
	public void run(String... args) throws Exception {
		buildAdminUser();
		buildTestUser();
	}
	
	private void buildAdminUser() {
		UserService userService = UserServiceFactory.get();
		User user = userService.getUserByLoginNameOrEmail(username, null);
		if (user != null) {
			return;
		}
		user = new User();
		user.setLoginName(username);
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(password, salt);
		Date time = new Date();
		user.setNickname(username);
		user.setRemark(username);
		user.setCellphone(username);
		user.setEmail(username);
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		user.setAdmin(true);
		user.setStatus(UserStatuses.NORMAL.getStatus());
		userService.saveOrUpdate(user);
	}
	
	private void buildTestUser() {
		UserService userService = UserServiceFactory.get();
		User user = userService.getUserByLoginNameOrEmail(TEST_USERNAME, null);
		if (user != null) {
			return;
		}
		user = new User();
		user.setLoginName(TEST_USERNAME);
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(TEST_PASSWORD, salt);
		Date time = new Date();
		user.setNickname(TEST_USERNAME);
		user.setRemark(TEST_USERNAME);
		user.setCellphone(TEST_USERNAME);
		user.setEmail(TEST_USERNAME);
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		user.setAdmin(false);
		user.setStatus(UserStatuses.NORMAL.getStatus());
		userService.saveOrUpdate(user);
	}
	
}
