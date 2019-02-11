package net.saisimon.agtms.mongodb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface NavigationMongodbRepository extends BaseMongoRepository<Navigation, Long> {
	
	List<Navigation> findByOperatorId(long userId);
	
	List<Navigation> findByIdInAndOperatorId(List<Long> ids, long operatorId);
	
	Navigation findByIdAndOperatorId(Long id, long operatorId);
	
	Navigation findByTitleAndOperatorId(String title, long operatorId);
	
	boolean existsByTitleAndOperatorId(String title, long operatorId);
	
	List<Navigation> findByParentIdAndOperatorId(Long parentId, long operatorId);
	
}
