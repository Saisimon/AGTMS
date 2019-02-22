package net.saisimon.agtms.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.order.BaseOrder;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.jpa.repository.TemplateJapRepository;

@Service
public class TemplateJpaService implements TemplateService, BaseOrder {
	
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
	
}
