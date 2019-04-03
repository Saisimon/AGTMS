package net.saisimon.agtms.jpa.token;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.token.Token;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.UserTokenJpaRepository;

@Service
public class JpaToken implements Token, JpaOrder {
	
	@Autowired
	private UserTokenJpaRepository userTokenJpaRepository;

	@Transactional(rollbackOn=Exception.class)
	@Override
	public UserToken getToken(Long uid) {
		if (uid == null) {
			return null;
		}
		Optional<UserToken> optional = userTokenJpaRepository.findByUserId(uid);
		if (!optional.isPresent()) {
			return null;
		}
		UserToken token = optional.get();
		if (token.getToken() == null || token.getExpireTime() == null || token.getExpireTime() < System.currentTimeMillis()) {
			return null;
		}
		token.setExpireTime(AuthUtils.getExpireTime());
		userTokenJpaRepository.saveOrUpdate(token);
		return token;
	}

	@Transactional(rollbackOn=Exception.class)
	@Override
	public void setToken(Long uid, UserToken token) {
		if (uid == null) {
			return;
		}
		if (token == null) {
			token = new UserToken();
			token.setUserId(uid);
		}
		userTokenJpaRepository.saveOrUpdate(token);
	}
	
}
