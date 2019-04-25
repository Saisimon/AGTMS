package net.saisimon.agtms.jpa.repository;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.jpa.domain.TestEmbeddable;
import net.saisimon.agtms.jpa.domain.TestEmbeddable.TestEmbeddablePK;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TestEmbeddableRepository extends BaseJpaRepository<TestEmbeddable, TestEmbeddablePK> {

}

