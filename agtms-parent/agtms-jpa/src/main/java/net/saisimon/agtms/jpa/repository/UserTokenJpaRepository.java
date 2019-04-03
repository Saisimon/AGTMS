package net.saisimon.agtms.jpa.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface UserTokenJpaRepository extends BaseJpaRepository<UserToken, Long> {
	
	Optional<UserToken> findByUserId(Long userId);
	
}
