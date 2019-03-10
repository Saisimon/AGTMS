package net.saisimon.agtms.jpa.repository.base;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class BaseJpaRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable> 
	extends JpaRepositoryFactoryBean<T, S, ID> {

	public BaseJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new SimpleJpaRepositoryFactory(entityManager);
	}

	private static class SimpleJpaRepositoryFactory extends JpaRepositoryFactory {
		
		public SimpleJpaRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}
		
		

		@Override
		protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
			JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
			return new SimpleBaseJpaRepository<>(entityInformation, entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return SimpleBaseJpaRepository.class;
		}
		
	}
	
}
