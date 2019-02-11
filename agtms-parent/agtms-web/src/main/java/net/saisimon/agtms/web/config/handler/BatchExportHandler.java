package net.saisimon.agtms.web.config.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.FileConstant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

@Slf4j
public class BatchExportHandler {
	
	private String getType(Map<String, Object> request) {
		Object typeObj = request.get("type");
		return typeObj == null ? "xlsx" : typeObj.toString();
	}
	
	private List<String> getFields(Map<String, Object> request) {
		Object fieldsObj = request.get("fields");
		if (fieldsObj == null) {
			return null;
		}
		return SystemUtils.transformList(fieldsObj, String.class);
	}

	@SuppressWarnings("unchecked")
	private List<Domain> getDatas(Map<String, Object> request, Template template) {
		List<Domain> domains = new ArrayList<>();
		try {
			GenerateService generateService = GenerateServiceFactory.build(template);
			Object selectObj = request.get("select");
			if (selectObj != null) {
				List<Long> select = SystemUtils.transformList(selectObj, Long.class);
				if (!CollectionUtils.isEmpty(select)) {
					for (Long id : select) {
						Domain domain = generateService.findById(id, template.getOperatorId());
						if (domain != null) {
							domains.add(domain);
						}
					}
				}
			} else {
				FilterRequest filter = FilterRequest.build((Map<String, Object>)request.get("filter"), TemplateUtils.getFilters(template));
				filter.and(Constant.OPERATORID, template.getOperatorId());
				List<Domain> ds = generateService.findList(filter, null);
				if (ds != null) {
					domains.addAll(ds);
				}
			}
			return domains;
		} catch (Exception e) {
			log.error("Export " + template.getId() + " Error", e);
			return null;
		}
	}
	
	private String exportDatas(Template template, List<String> fields, List<Domain> domains, String type) throws IOException {
		List<String> heads = new ArrayList<>();
		List<String> fieldNames = new ArrayList<>();
		List<List<Object>> datas = new ArrayList<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (!CollectionUtils.isEmpty(column.getFields())) {
					for (TemplateField field : column.getFields()) {
						String fieldName = column.getColumnName() + field.getFieldName();
						if (fields.contains(fieldName)) {
							fieldNames.add(fieldName);
							heads.add(field.getFieldTitle());
						}
					}
				}
			}
		}
		for (Domain domain : domains) {
			List<Object> data = new ArrayList<>();
			for (String fieldName : fieldNames) {
				Object value = domain.getField(fieldName);
				data.add(value == null ? "" : value);
			}
			datas.add(data);
		}
		try {
			File file;
			switch (type) {
			case FileConstant.XLS:
				file = FileUtils.toXLS(FileConstant.EXPORT_PATH, template.getId().toString(), heads, datas, null);
				break;
			case FileConstant.CSV:
				file = FileUtils.toCSV(FileConstant.EXPORT_PATH, template.getId().toString(), heads, datas, ",");
				break;
			case FileConstant.JSON:
				file = FileUtils.toJSON(FileConstant.EXPORT_PATH, template.getId().toString(), heads, datas);
				break;
			default:
				file = FileUtils.toXLSX(FileConstant.EXPORT_PATH, template.getId().toString(), heads, datas, null);
				break;
			}
			return file.getName();
		} catch (IOException e) {
			log.error("Export Excel Error", e);
			return null;
		}
	}
	
}
