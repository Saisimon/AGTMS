package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Notification;
import net.saisimon.agtms.core.service.NotificationService;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.NotificationMongodbRepository;
import net.saisimon.agtms.mongodb.repository.base.BaseMongodbRepository;

@Service
public class NotificationMongodbService implements NotificationService, MongodbOrder {

	@Autowired
	private NotificationMongodbRepository notificationMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseMongodbRepository<Notification, Long> getRepository() {
		return notificationMongodbRepository;
	}
	
	@Override
	public Notification saveOrUpdate(Notification entity) {
		if (entity == null) {
			return entity;
		}
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(notificationMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return notificationMongodbRepository.saveOrUpdate(entity);
	}

}
