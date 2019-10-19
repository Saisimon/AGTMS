package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Notification;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface NotificationJpaRepository extends BaseJpaRepository<Notification, Long> {
	
}
