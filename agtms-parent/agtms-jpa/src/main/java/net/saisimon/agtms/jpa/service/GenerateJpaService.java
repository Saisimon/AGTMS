package net.saisimon.agtms.jpa.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.jpa.repository.GenerateJpaRepository;
import net.saisimon.agtms.jpa.service.ddl.DdlService;

@Service
public class GenerateJpaService implements GenerateService {
	
	private static final Sign JPA_SIGN = Sign.builder().name("jpa").text("JPA").order(0).build();
	
	@Autowired
	private GenerateJpaRepository generateJpaRepository;
	@Autowired
	private DdlService ddlService;

	@Override
	public AbstractGenerateRepository getRepository() {
		return generateJpaRepository;
	}

	@Override
	public Sign sign() {
		return JPA_SIGN;
	}

	@Override
	public boolean createTable() {
		Template template = template();
		String tableName = TemplateUtils.getTableName(template);
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		boolean result = ddlService.createTable(tableName, fieldInfoMap);
		if (result) {
			createIndexes(tableName, fieldInfoMap);
		}
		return result;
	}

	@Override
	public boolean alterTable(Template oldTemplate) {
		if (oldTemplate == null) {
			return false;
		}
		Template template = template();
		String tableName = TemplateUtils.getTableName(template);
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		Map<String, TemplateField> oldFieldInfoMap = TemplateUtils.getFieldInfoMap(oldTemplate);
		boolean result = ddlService.alterTable(tableName, fieldInfoMap, oldFieldInfoMap);
		if (result) {
			alterIndexes(tableName, fieldInfoMap, oldFieldInfoMap);
		}
		return result;
	}

	@Override
	public boolean dropTable() {
		return ddlService.dropTable(TemplateUtils.getTableName(template()));
	}

	@Override
	public boolean createIndex(String tableName, String columnName, boolean unique) {
		return ddlService.createIndex(tableName, columnName, unique);
	}

	@Override
	public boolean dropIndex(String tableName, String columnName) {
		return ddlService.dropIndex(tableName, columnName);
	}
	
}
