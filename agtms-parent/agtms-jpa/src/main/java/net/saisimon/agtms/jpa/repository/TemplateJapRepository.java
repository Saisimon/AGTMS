package net.saisimon.agtms.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.jpa.repository.base.BaseJpaRepository;

@Repository
public interface TemplateJapRepository extends BaseJpaRepository<Template, Long> {

	@Override
	@Query("SELECT t FROM Template t JOIN FETCH t.columns c JOIN FETCH c.fields f WHERE t.id = ?1 ")
	Optional<Template> find(Long id);
	
}
