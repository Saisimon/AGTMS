package net.saisimon.agtms.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.repository.AbstractGenerateRepository;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.mongodb.repository.GenerateMongodbRepository;

@Service
public class GenerateMongodbService implements GenerateService {
	
	private static final Sign MONGODB_SIGN = Sign.builder().name("mongodb").text("MONGODB").order(Ordered.HIGHEST_PRECEDENCE).build();
	
	@Autowired
	private GenerateMongodbRepository generateMongoRepository;
	@Autowired
	private SequenceService sequenceService;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Sign sign() {
		return MONGODB_SIGN;
	}

	@Override
	public AbstractGenerateRepository getRepository() {
		return generateMongoRepository;
	}
	
	@Override
	public Domain saveDomain(Domain domain, Long operatorId) {
		Assert.notNull(domain, "domain can not be null");
		Long id = sequenceService.nextId(TemplateUtils.getTableName(template()));
		domain.setField(Constant.ID, id, Long.class);
		return GenerateService.super.saveDomain(domain, operatorId);
	}

	@Override
	public boolean createTable() {
		Template template = template();
		createIndexes(TemplateUtils.getTableName(template), TemplateUtils.getFieldInfoMap(template));
		return true;
	}

	@Override
	public boolean alterTable(Template oldTemplate) {
		if (oldTemplate == null) {
			return false;
		}
		Template template = template();
		alterIndexes(TemplateUtils.getTableName(template), TemplateUtils.getFieldInfoMap(template), TemplateUtils.getFieldInfoMap(oldTemplate));
		return true;
	}

	@Override
	public boolean dropTable() {
		mongoTemplate.dropCollection(TemplateUtils.getTableName(template()));
		return true;
	}
	
	@Override
	public boolean createIndex(String tableName, String columnName, boolean unique) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return false;
		}
		String indexName = tableName + "_" + columnName + "_idx";
		IndexOperations indexOperations = mongoTemplate.indexOps(tableName);
		Index index = new Index(columnName, Direction.ASC);
		index.named(indexName);
		index.background();
		if (unique) {
			index.unique();
		}
		indexOperations.ensureIndex(index);
		return true;
	}
	
	@Override
	public boolean dropIndex(String tableName, String columnName) {
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(columnName)) {
			return false;
		}
		String indexName = tableName + "_" + columnName + "_idx";
		IndexOperations indexOperations = mongoTemplate.indexOps(tableName);
		indexOperations.dropIndex(indexName);
		return true;
	}

}
