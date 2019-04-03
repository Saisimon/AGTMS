package net.saisimon.agtms.mongodb.token;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.token.Token;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.UserTokenMongodbRepository;

@Service
public class MongodbToken implements Token, MongodbOrder {
	
	@Autowired
	private UserTokenMongodbRepository userTokenMongodbRepository;

	@Override
	public UserToken getToken(Long uid) {
		if (uid == null) {
			return null;
		}
		Optional<UserToken> optional = userTokenMongodbRepository.findByUserId(uid);
		if (!optional.isPresent()) {
			return null;
		}
		UserToken token = optional.get();
		if (token.getToken() == null || token.getExpireTime() == null || token.getExpireTime() < System.currentTimeMillis()) {
			return null;
		}
		token.setExpireTime(AuthUtils.getExpireTime());
		userTokenMongodbRepository.saveOrUpdate(token);
		return token;
	}

	@Override
	public void setToken(Long uid, UserToken token) {
		if (uid == null) {
			return;
		}
		if (token == null) {
			token = new UserToken();
			token.setUserId(uid);
		}
		userTokenMongodbRepository.saveOrUpdate(token);
	}

}
