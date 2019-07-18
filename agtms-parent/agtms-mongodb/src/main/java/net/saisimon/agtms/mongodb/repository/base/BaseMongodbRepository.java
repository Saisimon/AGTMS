package net.saisimon.agtms.mongodb.repository.base;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import net.saisimon.agtms.core.repository.BaseRepository;

@NoRepositoryBean
public interface BaseMongodbRepository<T, ID> extends MongoRepository<T, ID>, BaseRepository<T, ID> {
	
	String getCollectionName();
	
}
