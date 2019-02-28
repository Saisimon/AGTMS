package net.saisimon.agtms.mongodb.repository.base;

import java.io.Serializable;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class BaseMongodbRepositoryFactoryBean<T extends MongoRepository<S, ID>, S, ID extends Serializable> 
	extends MongoRepositoryFactoryBean<T, S, ID> {

	public BaseMongodbRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
		return new SimpleMongoRepositoryFactory(operations);
	}
	
	private static class SimpleMongoRepositoryFactory extends MongoRepositoryFactory {
		
		private MongoOperations mongoOperations;

		public SimpleMongoRepositoryFactory(MongoOperations mongoOperations) {
			super(mongoOperations);
			this.mongoOperations = mongoOperations;
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return SimpleBaseMongodbRepository.class;
		}

		@Override
		protected Object getTargetRepository(RepositoryInformation information) {
			MongoEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
			return new SimpleBaseMongodbRepository<>(entityInformation, mongoOperations);
		}
		
	}
	
}
