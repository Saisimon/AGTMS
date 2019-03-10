package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.OperationService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.OperationJpaRepository;

@Service
public class OperationJpaService implements OperationService, JpaOrder {
	
	@Autowired
	private OperationJpaRepository operationJpaRepository;

	@Override
	public BaseRepository<Operation, Long> getRepository() {
		return operationJpaRepository;
	}

}
