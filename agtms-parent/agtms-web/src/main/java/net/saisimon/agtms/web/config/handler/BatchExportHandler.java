package net.saisimon.agtms.web.config.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.FileConstant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.enums.DataSources;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;

@Slf4j
public class BatchExportHandler extends BasicWebSocketHandler {
	
	private int totalCount;
	private AtomicInteger currentCount;
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String json = new String(message.asBytes(), "UTF-8");
		@SuppressWarnings("unchecked")
		Map<String, Object> request = SystemUtils.fromJson(json, Map.class, String.class, Object.class);
		Template template = getTemplate(request, session);
		if (template == null) {
			session.close(CloseStatus.BAD_DATA);
			log.warn("template not found");
			return;
		}
		List<String> fields = getFields(request);
		if (CollectionUtils.isEmpty(fields)) {
			session.close(CloseStatus.BAD_DATA);
			log.warn("missing fields");
			return;
		}
		List<Domain> domains = getDatas(request, template);
		if (domains == null) {
			session.close(CloseStatus.SERVER_ERROR);
			log.warn("get datas failed");
			return;
		}
		currentCount = new AtomicInteger(domains.size());
		totalCount = domains.size() * 2 + 2;
		session.sendMessage(new TextMessage(String.valueOf((currentCount.get() * 100.0 / totalCount))));
		String exportName = exportDatas(template, fields, domains, getType(request), session);
		if (StringUtils.isBlank(exportName)) {
			session.close(CloseStatus.SERVER_ERROR);
			log.warn("export file failed");
			return;
		}
		String url = "/manage/" + template.getId() + "/export/download?filename=" + exportName;
		session.sendMessage(new TextMessage(url));
	}
	
	private String getType(Map<String, Object> request) {
		Object typeObj = request.get("type");
		return typeObj == null ? "xlsx" : typeObj.toString();
	}
	
	private List<String> getFields(Map<String, Object> request) {
		Object fieldsObj = request.get("fields");
		if (fieldsObj == null) {
			return null;
		}
		return SystemUtils.transform(fieldsObj, String.class);
	}

	@SuppressWarnings("unchecked")
	private List<Domain> getDatas(Map<String, Object> request, Template template) {
		List<Domain> domains = new ArrayList<>();
		try {
			GenerateService generateService = GenerateServiceFactory.build(template);
			Object selectObj = request.get("select");
			if (selectObj != null) {
				List<Long> select = SystemUtils.transform(selectObj, Long.class);
				if (!CollectionUtils.isEmpty(select)) {
					for (Long id : select) {
						Domain domain = generateService.findById(id, template.getUserId());
						if (domain != null) {
							domains.add(domain);
						}
					}
				}
			} else {
				FilterRequest filter = FilterRequest.build((Map<String, Object>)request.get("filter"));
				if (!DataSources.RPC.getSource().equals(template.getSource())) {
					filter.and("creator", template.getUserId());
				}
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
	
	private String exportDatas(Template template, List<String> fields, List<Domain> domains, String type, WebSocketSession session) throws IOException {
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
		session.sendMessage(new TextMessage(String.valueOf((currentCount.incrementAndGet() * 100.0 / totalCount))));
		for (Domain domain : domains) {
			List<Object> data = new ArrayList<>();
			for (String fieldName : fieldNames) {
				Object value = domain.getField(fieldName);
				data.add(value == null ? "" : value);
			}
			datas.add(data);
			session.sendMessage(new TextMessage(String.valueOf((currentCount.incrementAndGet() * 100.0 / totalCount))));
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
			session.sendMessage(new TextMessage(String.valueOf((currentCount.incrementAndGet() * 100.0 / totalCount))));
			return file.getName();
		} catch (IOException e) {
			log.error("Export Excel Error", e);
			return null;
		}
	}
	
}
