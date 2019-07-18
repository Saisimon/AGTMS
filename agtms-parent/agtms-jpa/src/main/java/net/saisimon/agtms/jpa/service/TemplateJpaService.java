package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.TemplateJapRepository;

@Service
@ConditionalOnMissingClass("org.apache.shardingsphere.api.hint.HintManager")
public class TemplateJpaService implements TemplateService, JpaOrder {
	
	@Autowired
	private TemplateJapRepository templateJpaRepository;

	@Override
	public BaseRepository<Template, Long> getRepository() {
		return templateJpaRepository;
	}

}
