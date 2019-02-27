package net.saisimon.agtms.example.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.example.domain.Test;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TestRepository extends BaseJpaRepository<Test, Integer> {
	
}
