package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.jpa.domain.TestLong;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TestLongRepository extends BaseJpaRepository<TestLong, Long> {

}
