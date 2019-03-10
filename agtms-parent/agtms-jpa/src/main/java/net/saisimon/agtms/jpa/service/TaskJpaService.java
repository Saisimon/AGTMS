package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.TaskJpaRepository;

@Service
public class TaskJpaService implements TaskService, JpaOrder {
	
	@Autowired
	private TaskJpaRepository taskJpaRepository;

	@Override
	public BaseRepository<Task, Long> getRepository() {
		return taskJpaRepository;
	}

}
