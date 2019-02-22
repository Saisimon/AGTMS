package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface NavigationJpaRepository extends BaseJpaRepository<Navigation, Long> {
	
}
