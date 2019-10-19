package net.saisimon.agtms.mongodb.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Notification;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Repository
public interface NotificationMongodbRepository extends BaseMongodbRepository<Notification, Long> {
	
}
