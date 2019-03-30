package net.saisimon.agtms.jpa.token;

import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.dto.TokenInfo;
import net.saisimon.agtms.core.token.Token;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.service.UserJpaService;

@Service
public class JpaToken implements Token, JpaOrder {
	
	@Autowired
	private UserJpaService userJpaService;

	@Transactional(rollbackOn=Exception.class)
	@Override
	public TokenInfo getTokenInfo(Long uid) {
		if (uid == null) {
			return null;
		}
		Optional<User> optional = userJpaService.findById(uid);
		if (!optional.isPresent()) {
			return null;
		}
		User user = optional.get();
		if (user.getToken() == null || user.getExpireTime() == null || user.getExpireTime() < System.currentTimeMillis()) {
			return null;
		}
		Map<String, Object> updateMap = MapUtil.newHashMap(1);
		updateMap.put("expireTime", AuthUtils.getExpireTime());
		userJpaService.update(user.getId(), updateMap);
		return AuthUtils.buildTokenInfo(user);
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void setTokenInfo(Long uid, TokenInfo tokenInfo) {
		if (uid == null) {
			return;
		}
		Optional<User> optional = userJpaService.findById(uid);
		if (!optional.isPresent()) {
			return;
		}
		Map<String, Object> updateMap = MapUtil.newHashMap(2);
		if (tokenInfo == null) {
			updateMap.put("expireTime", null);
			updateMap.put("token", null);
		} else {
			updateMap.put("expireTime", tokenInfo.getExpireTime());
			updateMap.put("token", tokenInfo.getToken());
		}
		userJpaService.update(uid, updateMap);
	}
	
}
