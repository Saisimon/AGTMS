package net.saisimon.agtms.core.token;

import net.saisimon.agtms.core.domain.entity.UserToken;

public interface Token {
	
	UserToken getToken(Long uid, boolean update);
	
	void setToken(Long uid, UserToken token);
	
}
