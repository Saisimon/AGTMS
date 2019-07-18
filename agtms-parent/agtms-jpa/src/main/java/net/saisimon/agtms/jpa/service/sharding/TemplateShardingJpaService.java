package net.saisimon.agtms.jpa.service.sharding;

import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.jpa.order.JpaOrder;
import net.saisimon.agtms.jpa.repository.TemplateJapRepository;

@Service
@ConditionalOnClass(HintManager.class)
public class TemplateShardingJpaService implements TemplateService, JpaOrder {

	@Autowired
	private TemplateJapRepository templateJpaRepository;

	@Override
	public BaseRepository<Template, Long> getRepository() {
		return templateJpaRepository;
	}

	@Override
	public Template saveOrUpdate(Template entity) {
		if (entity == null) {
			return null;
		}
		try (HintManager hintManager = HintManager.getInstance()) {
			hintManager.addDatabaseShardingValue(Template.TemplateColumn.TABLE_NAME, entity.getOperatorId());
			hintManager.addDatabaseShardingValue(Template.TemplateField.TABLE_NAME, entity.getOperatorId());
			return TemplateService.super.saveOrUpdate(entity);
		}
	}

}
