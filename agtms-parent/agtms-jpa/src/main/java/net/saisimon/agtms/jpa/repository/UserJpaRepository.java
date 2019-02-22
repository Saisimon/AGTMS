package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface UserJpaRepository extends BaseJpaRepository<User, Long> {
	
}
