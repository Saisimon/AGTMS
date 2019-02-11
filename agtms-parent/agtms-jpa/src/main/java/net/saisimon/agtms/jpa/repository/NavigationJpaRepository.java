package net.saisimon.agtms.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface NavigationJpaRepository extends BaseJpaRepository<Navigation, Long> {
	
	List<Navigation> findByOperatorId(Long operatorId);
	
	List<Navigation> findByIdInAndOperatorId(List<Long> ids, Long operatorId);
	
	Navigation findByIdAndOperatorId(Long id, Long operatorId);
	
	Navigation findByTitleAndOperatorId(String title, Long operatorId);
	
	boolean existsByTitleAndOperatorId(String title, Long operatorId);
	
	List<Navigation> findByParentIdAndOperatorId(Long parentId, Long operatorId);
	
}
