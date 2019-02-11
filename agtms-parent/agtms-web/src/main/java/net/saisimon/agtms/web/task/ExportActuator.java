package net.saisimon.agtms.web.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.FileConstant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Task;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;

@Component
@Slf4j
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
		File file = null;
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
			case FileConstant.XLSX:
				file = FileUtils.toXLSX(path, name, heads, datas, null);
				break;
			default:
				break;
		}
		if (file != null) {
			return ResultUtils.success(name);
		} else {
			return ResultUtils.error(500, "export.failed");
		}
	}
	
	@Override
	public void download(Task task, HttpServletRequest request, HttpServletResponse response) {
		if (task == null || task.getHandleStatus() != HandleStatuses.SUCCESS.getStatus()) {
			return;
		}
		ExportParam param = SystemUtils.fromJson(task.getTaskParam(), ExportParam.class);
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			return;
		}
		String userAgent = request.getHeader("user-agent");
		String path = FileConstant.EXPORT_PATH + File.separatorChar + param.getUserId();
		File file = null;
		String filename = template.getTitle();
		switch (param.getExportFileType()) {
			case FileConstant.XLS:
				response.setContentType("application/vnd.ms-excel");
				file = new File(path + File.separatorChar + task.getHandleResult() + FileConstant.XLS_SUFFIX);
				filename += FileConstant.XLS_SUFFIX;
				break;
			case FileConstant.CSV:
				response.setContentType("application/CSV");
				file = new File(path + File.separatorChar + task.getHandleResult() + FileConstant.CSV_SUFFIX);
				filename += FileConstant.CSV_SUFFIX;
				break;
			case FileConstant.JSON:
				response.setContentType("application/json");
				file = new File(path + File.separatorChar + task.getHandleResult() + FileConstant.JSON_SUFFIX);
				filename += FileConstant.JSON_SUFFIX;
				break;
			case FileConstant.XLSX:
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				file = new File(path + File.separatorChar + task.getHandleResult() + FileConstant.XLSX_SUFFIX);
				filename += FileConstant.XLSX_SUFFIX;
				break;
			default:
				break;
		}
		if (file == null || !file.exists()) {
			return;
		}
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			response.setHeader("Content-Disposition", SystemUtils.encodeDownloadContentDisposition(userAgent, filename));
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			log.error("响应流异常", e);
			return;
		}
	}

	@Override
	public Sign sign() {
		return EXPORT_SIGN;
	}

}
