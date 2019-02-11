package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Task;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TaskJpaRepository extends BaseJpaRepository<Task, Long> {
	
}
