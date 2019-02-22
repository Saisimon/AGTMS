package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface NavigationMongodbRepository extends BaseMongoRepository<Navigation, Long> {
	
}
