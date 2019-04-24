package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.jpa.domain.TestIdClass;
import net.saisimon.agtms.jpa.domain.TestIdClass.TestIdClassPK;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TestIdClassRepository extends BaseJpaRepository<TestIdClass, TestIdClassPK> {
	
}
