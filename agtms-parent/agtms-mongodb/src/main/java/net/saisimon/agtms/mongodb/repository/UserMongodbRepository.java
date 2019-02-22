package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface UserMongodbRepository extends BaseMongoRepository<User, Long> {
	
}
