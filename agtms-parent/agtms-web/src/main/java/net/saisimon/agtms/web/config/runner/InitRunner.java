package net.saisimon.agtms.web.config.runner;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;

/**
 * 初始化操作
 * 
 * @author saisimon
 *
 */
public class InitRunner implements CommandLineRunner {
	
	private static final String TEST_USERNAME = "test";
	private static final String TEST_PASSWORD = "test";
	
	@Autowired
	private AgtmsProperties agtmsProperties;

	@Override
	@Transactional(rollbackOn=Exception.class)
	public void run(String... args) throws Exception {
		buildAdminUser();
		buildTestUser();
	}
	
	private void buildAdminUser() {
		UserService userService = UserServiceFactory.get();
		User user = userService.getUserByLoginNameOrEmail(agtmsProperties.getAdminUsername(), null);
		if (user != null) {
			return;
		}
		user = new User();
		user.setLoginName(agtmsProperties.getAdminUsername());
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(agtmsProperties.getAdminPassword(), salt);
		Date time = new Date();
		user.setNickname(agtmsProperties.getAdminUsername());
		user.setRemark(agtmsProperties.getAdminUsername());
		user.setCellphone(agtmsProperties.getAdminUsername());
		user.setEmail(agtmsProperties.getAdminUsername());
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
