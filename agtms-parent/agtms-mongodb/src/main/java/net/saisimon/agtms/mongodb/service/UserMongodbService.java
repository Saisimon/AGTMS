package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.UserMongodbRepository;

@Service
public class UserMongodbService implements UserService, MongodbOrder {

	@Autowired
	private UserMongodbRepository userMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseRepository<User, Long> getRepository() {
		return userMongodbRepository;
	}
	
	@Override
	public User saveOrUpdate(User entity) {
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(userMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return UserService.super.saveOrUpdate(entity);
	}
	
}
