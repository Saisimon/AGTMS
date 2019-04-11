package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface UserTokenMongodbRepository extends BaseMongodbRepository<UserToken, Long> {
	
}
