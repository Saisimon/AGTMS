package net.saisimon.agtms.jpa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.order.AbstractOrder;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.jpa.repository.TemplateJapRepository;

@Service
public class TemplateJpaService extends AbstractOrder implements TemplateService {
	
	@Autowired
	private TemplateJapRepository templateJpaRepository;
	@Autowired
	private DdlService ddlService;

	@Override
	public BaseRepository<Template, Long> getRepository() {
		return templateJpaRepository;
	}

	@Override
	public boolean exists(String title, Long userId) {
		return templateJpaRepository.existsByTitleAndUserId(title, userId);
	}

	@Override
	public List<Template> getTemplates(Long navigationId, Long userId) {
		return templateJpaRepository.findByNavigationIdAndUserId(navigationId, userId);
	}

	@Override
	public void createTable(Template template) {
		ddlService.createTable(template);
	}

	@Override
	public void alterTable(Template template, Template oldTemplate) {
		ddlService.alterTable(template, oldTemplate);
	}

	@Override
	public void dropTable(Template template) {
		ddlService.dropTable(template);
	}
	
}
