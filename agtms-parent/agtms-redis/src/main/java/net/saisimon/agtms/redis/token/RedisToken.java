package net.saisimon.agtms.redis.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.dto.TokenInfo;
import net.saisimon.agtms.core.token.Token;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.redis.order.RedisOrder;

@Service
public class RedisToken implements RedisOrder, Token {
	
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public TokenInfo getTokenInfo(Long uid) {
		if (uid == null) {
			return null;
		}
		String json = redisTemplate.opsForValue().get(uid.toString());
		if (StringUtils.isBlank(json)) {
			return null;
		}
		TokenInfo tokeInfo = SystemUtils.fromJson(json, TokenInfo.class);
		if (tokeInfo == null || tokeInfo.getToken() == null || tokeInfo.getExpireTime() == null || tokeInfo.getExpireTime() < System.currentTimeMillis()) {
			redisTemplate.delete(uid.toString());
			return null;
		}
		tokeInfo.setExpireTime(AuthUtils.getExpireTime());
		setTokenInfo(uid, tokeInfo);
		return tokeInfo;
	}

	@Override
	public void setTokenInfo(Long uid, TokenInfo tokenInfo) {
		if (uid == null) {
			return;
		}
		if (tokenInfo == null) {
			redisTemplate.delete(uid.toString());
		} else {
			redisTemplate.opsForValue().set(uid.toString(), SystemUtils.toJson(tokenInfo));
		}
	}

}
