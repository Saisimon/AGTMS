package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface UserMongodbRepository extends BaseMongodbRepository<User, Long> {
	
}
