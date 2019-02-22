package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.order.BaseOrder;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.jpa.repository.UserJpaRepository;

@Service
public class UserJpaService implements UserService, BaseOrder {
	
	@Autowired
	private UserJpaRepository userJpaRepository;
	
	@Override
	public BaseRepository<User, Long> getRepository() {
		return userJpaRepository;
	}
	
}
