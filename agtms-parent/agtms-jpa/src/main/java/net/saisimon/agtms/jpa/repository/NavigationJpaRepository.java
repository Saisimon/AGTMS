package net.saisimon.agtms.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface NavigationJpaRepository extends BaseJpaRepository<Navigation, Long> {
	
	List<Navigation> findByBelong(Long userId);
	
	List<Navigation> findByIdInAndBelong(List<Long> ids, Long userId);
	
	Navigation findByIdAndBelong(Long id, Long userId);
	
	Navigation findByTitleAndBelong(String title, Long userId);
	
	boolean existsByTitleAndBelong(String title, Long userId);
	
	List<Navigation> findByParentIdAndBelong(Long parentId, Long userId);
	
}
