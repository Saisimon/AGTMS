package net.saisimon.agtms.jpa.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface UserTokenJpaRepository extends BaseJpaRepository<UserToken, Long> {
	
	@Transactional
	@Modifying
	void deleteByUserId(Long userId);
	
}
