package net.saisimon.agtms.mongodb.repository.base;

import java.util.List;

import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import net.saisimon.agtms.core.repository.BaseRepository;

@NoRepositoryBean
public interface BaseMongoRepository<T, ID> extends MongoRepository<T, ID>, BaseRepository<T, ID> {
	
	String getCollectionName();
	
	Boolean existCollection();
	
	void createCollection();
	
	void dropCollection();
	
	List<IndexInfo> getIndexes();
	
	String createIndex(IndexDefinition indexDefinition);
	
	void dropIndex(String name);
	
}
