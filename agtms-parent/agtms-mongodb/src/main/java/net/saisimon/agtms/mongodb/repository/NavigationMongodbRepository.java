package net.saisimon.agtms.mongodb.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.mongodb.repository.base.BaseMongoRepository;

@Repository
public interface NavigationMongodbRepository extends BaseMongoRepository<Navigation, Long> {
	
	List<Navigation> findByBelong(long userId);
	
	List<Navigation> findByIdInAndBelong(List<Long> ids, long userId);
	
	Navigation findByIdAndBelong(Long id, long userId);
	
	Navigation findByTitleAndBelong(String title, long userId);
	
	boolean existsByTitleAndBelong(String title, long userId);
	
	List<Navigation> findByParentIdAndBelong(Long parentId, long userId);
	
}
