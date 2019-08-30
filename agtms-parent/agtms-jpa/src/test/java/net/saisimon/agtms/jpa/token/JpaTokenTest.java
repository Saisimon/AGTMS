package net.saisimon.agtms.jpa.token;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.jpa.JpaTestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaTestApplication.class, properties = { "spring.main.bannerMode=OFF", "logging.level.root=ERROR" })
@DataJpaTest(showSql=false)
@JdbcTest
public class JpaTokenTest {
	
	@Autowired
	private JpaToken jpaToken;
	@Autowired
	private UserService userService;
	
	private User user;
	
	@Before
	public void before() {
		user = new User();
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
		UserToken userToken = jpaToken.getToken(null, false);
		Assert.assertNull(userToken);
		
		userToken = jpaToken.getToken(null, true);
		Assert.assertNull(userToken);
		
		Long uid = user.getId();
		userToken = jpaToken.getToken(uid, true);
		Assert.assertNull(userToken);
		
		jpaToken.setToken(uid, null);
		
		Long expireTime = AuthUtils.getExpireTime();
		String token = AuthUtils.createToken();
		userToken = new UserToken();
		userToken.setExpireTime(expireTime);
		userToken.setToken(token);
		userToken.setUserId(uid);
		jpaToken.setToken(uid, userToken);
		
		userToken = jpaToken.getToken(uid, false);
		Assert.assertNotNull(userToken);
		Assert.assertEquals(uid, userToken.getUserId());
		Assert.assertEquals(token, userToken.getToken());
		Assert.assertEquals(expireTime, userToken.getExpireTime());
		
		userToken = jpaToken.getToken(uid, true);
		Assert.assertNotNull(userToken);
		Assert.assertEquals(uid, userToken.getUserId());
		Assert.assertEquals(token, userToken.getToken());
		Assert.assertTrue(userToken.getExpireTime() > expireTime);
		
		jpaToken.setToken(uid, null);
		userToken = jpaToken.getToken(uid, false);
		Assert.assertNull(userToken);
	}
	
}
