package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Notification;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.NotificationService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.NotificationJpaRepository;

@Service
public class NotificationJpaService implements NotificationService, JpaOrder {
	
	@Autowired
	private NotificationJpaRepository notificationJpaRepository;

	@Override
	public BaseRepository<Notification, Long> getRepository() {
		return notificationJpaRepository;
	}

}
