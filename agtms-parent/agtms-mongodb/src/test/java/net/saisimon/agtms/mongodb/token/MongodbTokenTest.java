package net.saisimon.agtms.mongodb.token;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.mongodb.MongodbTestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongodbTestApplication.class, properties = {"spring.main.banner-mode=OFF", "logging.level.net.saisimon=DEBUG", "logging.level.org.springframework.data.mongodb=DEBUG"})
@DataMongoTest
public class MongodbTokenTest {
	
	@Autowired
	private MongodbToken mongodbToken;
	@Autowired
	private UserService userService;
	
	private User user;
	
	@Before
	public void before() {
		user = new User();
		user.setAdmin(false);
		user.setAvatar("/");
		user.setCellphone("test");
		user.setEmail("test");
		user.setLoginName("test");
		user.setNickname("test");
		user.setPassword("test");
		user.setRemark("-");
		user.setSalt("test");
		user.setStatus(1);
		Date time = new Date();
		user.setCreateTime(time);
		user.setUpdateTime(time);
		userService.saveOrUpdate(user);
	}
	
	@After
	public void after() {
		userService.delete(user);
	}
	
	@Test
	public void test() {
		UserToken userToken = mongodbToken.getToken(null, false);
		Assert.assertNull(userToken);
		
		userToken = mongodbToken.getToken(null, true);
		Assert.assertNull(userToken);
		
		Long uid = user.getId();
		userToken = mongodbToken.getToken(uid, true);
		Assert.assertNull(userToken);
		
		mongodbToken.setToken(uid, null);
		
		Long expireTime = AuthUtils.getExpireTime();
		String token = AuthUtils.createToken();
		userToken = new UserToken();
		userToken.setExpireTime(expireTime);
		userToken.setToken(token);
		userToken.setUserId(uid);
		mongodbToken.setToken(uid, userToken);
		
		userToken = mongodbToken.getToken(uid, false);
		Assert.assertNotNull(userToken);
		Assert.assertEquals(uid, userToken.getUserId());
		Assert.assertEquals(token, userToken.getToken());
		Assert.assertEquals(expireTime, userToken.getExpireTime());
		
		userToken = mongodbToken.getToken(uid, true);
		Assert.assertNotNull(userToken);
		Assert.assertEquals(uid, userToken.getUserId());
		Assert.assertEquals(token, userToken.getToken());
		Assert.assertTrue(userToken.getExpireTime() > expireTime);
		
		mongodbToken.setToken(uid, null);
		userToken = mongodbToken.getToken(uid, false);
		Assert.assertNull(userToken);
	}
	
}
