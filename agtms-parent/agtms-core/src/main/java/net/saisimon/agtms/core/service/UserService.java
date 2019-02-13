package net.saisimon.agtms.core.service;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.User;

/**
 * 用户服务接口
 * 
 * @author saisimon
 *
 */
public interface UserService extends BaseService<User, Long>, Ordered {
	
	User auth(String username, String password);
	
	User register(String username, String email, String password);
	
}
