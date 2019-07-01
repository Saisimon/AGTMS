package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.TemplateJapRepository;
import net.saisimon.agtms.jpa.service.ddl.DdlService;

@Service
@ConditionalOnMissingClass("org.apache.shardingsphere.api.hint.HintManager")
public class TemplateJpaService implements TemplateService, JpaOrder {
	
	@Autowired
	private TemplateJapRepository templateJpaRepository;
	@Autowired
	private DdlService ddlService;

	@Override
	public BaseRepository<Template, Long> getRepository() {
		return templateJpaRepository;
	}

	@Override
	public boolean createTable(Template template) {
		return ddlService.createTable(template);
	}

	@Override
	public boolean alterTable(Template template, Template oldTemplate) {
		return ddlService.alterTable(template, oldTemplate);
	}

	@Override
	public boolean dropTable(Template template) {
		return ddlService.dropTable(template);
	}

	@Override
	public Template saveOrUpdate(Template entity) {
		return TemplateService.super.saveOrUpdate(entity);
	}
	
}
