package net.saisimon.agtms.web.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.FileConstant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;

@Component
public class ExportActuator implements Actuator<ExportParam> {
	
	private static final Sign EXPORT_SIGN = Sign.builder().name(Functions.EXPORT.getFunction()).text(Functions.EXPORT.getFunction()).build();
	
	@Override
	public Result execute(ExportParam param) throws Exception {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		List<String> heads = new ArrayList<>();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		List<String> fields = new ArrayList<>();
		for (String exportField : param.getExportFields()) {
			TemplateField templateField = fieldInfoMap.get(exportField);
			if (templateField != null) {
				heads.add(templateField.getFieldTitle());
				fields.add(exportField);
			}
		}
		GenerateService generateService = GenerateServiceFactory.build(template);
		FilterRequest filter = FilterRequest.build(param.getFilter(), TemplateUtils.getFilters(template));
		filter.and(Constant.OPERATORID, template.getOperatorId());
		List<Domain> domains = generateService.findList(filter, null);
		List<List<Object>> datas = new ArrayList<>();
		for (Domain domain : domains) {
			List<Object> data = new ArrayList<>();
			for (String field : fields) {
				Object value = domain.getField(field);
				data.add(value == null ? "" : value);
			}
			datas.add(data);
		}
		File file;
		String path = FileConstant.EXPORT_PATH + File.separatorChar + param.getUserId();
		String name = UUID.randomUUID().toString();
		switch (param.getExportFileType()) {
			case FileConstant.XLS:
				file = FileUtils.toXLS(path, name, heads, datas, null);
				break;
			case FileConstant.CSV:
				file = FileUtils.toCSV(path, name, heads, datas, ",");
				break;
			case FileConstant.JSON:
				file = FileUtils.toJSON(path, name, heads, datas);
				break;
			default:
				file = FileUtils.toXLSX(path, name, heads, datas, null);
				break;
		}
		if (file != null) {
			return ResultUtils.success(name);
		} else {
			return ResultUtils.error(500, "export.failed");
		}
	}

	@Override
	public Sign sign() {
		return EXPORT_SIGN;
	}

}
