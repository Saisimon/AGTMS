package net.saisimon.agtms.jpa.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface UserJpaRepository extends BaseJpaRepository<User, Long> {
	
	@Modifying
	@Query("update User u set u.expireTime = ?2, u.token = ?3 where u.id = ?1")
	int updateExpireTimeAndToken(Long id, Long expireTime, String token);
	
	
	
}
