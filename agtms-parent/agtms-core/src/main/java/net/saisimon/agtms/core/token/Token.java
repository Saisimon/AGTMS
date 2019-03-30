package net.saisimon.agtms.core.token;

import net.saisimon.agtms.core.dto.TokenInfo;

public interface Token {
	
	TokenInfo getTokenInfo(Long uid);
	
	void setTokenInfo(Long uid, TokenInfo tokenInfo);
	
}
