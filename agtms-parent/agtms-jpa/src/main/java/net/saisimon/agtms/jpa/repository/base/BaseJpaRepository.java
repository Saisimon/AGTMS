package net.saisimon.agtms.jpa.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import net.saisimon.agtms.core.repository.BaseRepository;

@NoRepositoryBean
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, BaseRepository<T, ID> {
	
}
