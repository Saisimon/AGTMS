package net.saisimon.agtms.redis.token;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.token.Token;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.redis.order.RedisOrder;

@Service
public class RedisToken implements RedisOrder, Token {
	
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public UserToken getToken(Long uid, boolean update) {
		if (uid == null) {
			return null;
		}
		String json = redisTemplate.opsForValue().get(uid.toString());
		if (SystemUtils.isBlank(json)) {
			return null;
		}
		UserToken token = SystemUtils.fromJson(json, UserToken.class);
		if (token == null || token.getToken() == null || token.getExpireTime() == null || token.getExpireTime() < System.currentTimeMillis()) {
			redisTemplate.delete(uid.toString());
			return null;
		}
		Optional<User> userOptional = UserServiceFactory.get().findById(token.getUserId());
		if (!userOptional.isPresent()) {
			redisTemplate.delete(uid.toString());
			return null;
		}
		if (update) {
			token.setExpireTime(AuthUtils.getExpireTime());
			setToken(uid, token);
		}
		User user = userOptional.get();
		token.setAdmin(user.isAdmin());
		token.setLoginName(user.getLoginName());
		token.setAvatar(user.getAvatar());
		return token;
	}

	@Override
	public void setToken(Long uid, UserToken token) {
		if (uid == null) {
			return;
		}
		if (token == null) {
			redisTemplate.delete(uid.toString());
		} else {
			redisTemplate.opsForValue().set(uid.toString(), SystemUtils.toJson(token));
		}
	}

}
