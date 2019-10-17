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
import javax.transaction.Transactional;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterParam;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.factory.FileHandlerFactory;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.util.FileUtils;

/**
 * 导出执行器
 * 
 * @author saisimon
 *
 */
@Component
public class ExportActuator implements Actuator<ExportParam> {
	
	private static final Sign EXPORT_SIGN = Sign.builder().name(Functions.EXPORT.getFunction()).text(Functions.EXPORT.getFunction()).build();
	private static final int PAGE_SIZE = 5000;
	
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private BasicProperties basicProperties;
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Result execute(ExportParam param) throws Exception {
		Template template = TemplateUtils.getTemplate(param.getTemplateId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		FilterRequest filter = FilterRequest.build(param.getFilter(), TemplateUtils.getFilters(template));
		if (filter == null) {
			filter = FilterRequest.build();
		}
		Long total = GenerateServiceFactory.build(template).count(filter);
		if (total > basicProperties.getExportRowsMaxSize()) {
			return ErrorMessage.Task.Export.TASK_EXPORT_MAX_SIZE_LIMIT;
		}
		exportDatas(template, param, filter);
		return ResultUtils.simpleSuccess();
	}

	@Override
	public String taskContent(ExportParam param) {
		return param == null ? null : param.getExportFileName();
	}
	
	@Override
	public String handleResult(String handleResult) {
		if (SystemUtils.isBlank(handleResult)) {
			return handleResult;
		}
		if (handleResult.equals(ErrorMessage.Task.Export.TASK_EXPORT_MAX_SIZE_LIMIT.getMessage())) {
			return getMessage(ErrorMessage.Task.Export.TASK_EXPORT_MAX_SIZE_LIMIT.getMessage(), basicProperties.getExportRowsMaxSize());
		} else {
			return getMessage(handleResult);
		}
	}
	
	@Override
	public void download(ExportParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (param == null || param.getUuid() == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		Template template = TemplateUtils.getTemplate(param.getTemplateId());
		if (template == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		File file = createExportFile(param, null);
		if (file == null || !file.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			response.setContentType(FileUtils.CONTENT_TYPE_MAP.get(param.getExportFileType()));
			response.setHeader("Content-Disposition", SystemUtils.encodeDownloadContentDisposition(request.getHeader("user-agent"), param.getExportFileName() + "." + param.getExportFileType()));
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
		}
	}
	
	@Override
	public void delete(ExportParam param) throws Exception {
		if (param == null) {
			return;
		}
		File file = createExportFile(param, null);
		if (file == null || !file.exists()) {
			return;
		}
		file.delete();
	}

	@Override
	public Sign sign() {
		return EXPORT_SIGN;
	}
	
	private File createExportFile(ExportParam param, Integer idx) throws IOException {
		StringBuilder exportFilePath = new StringBuilder();
		exportFilePath.append(basicProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.EXPORT_PATH)
			.append(File.separatorChar).append(param.getUserId());
		String fileName = idx == null ? param.getUuid() : param.getUuid() + "-" + idx.toString();
		return FileUtils.createFile(exportFilePath.toString(), fileName, "." + param.getExportFileType());
	}
	
	private void exportDatas(Template template, ExportParam param, FilterRequest filter) throws IOException, InterruptedException {
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		String[] exportFields = buildExportFields(param.getExportFields(), fieldInfoMap);
		List<Object> heads = buildHeads(exportFields, fieldInfoMap);
		param.setUuid(UUID.randomUUID().toString());
		FileHandler handler = FileHandlerFactory.getHandler(param.getExportFileType());
		if (handler == null) {
			return;
		}
		List<List<Object>> datas = new ArrayList<>();
		datas.add(heads);
		int idx = 0;
		Long lastId = null;
		List<File> files = new ArrayList<>();
		try {
			do {
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Task Cancel");
				}
				FilterPageable pageable = new FilterPageable(new FilterParam(Constant.ID, lastId, Operator.LT), PAGE_SIZE, null);
				List<Domain> domains = GenerateServiceFactory.build(template).findList(filter, pageable, exportFields);
				List<Map<String, Object>> domainList = DomainUtils.conversions(fieldInfoMap, template.getService(), domains);
				buildDatas(domainList, datas, exportFields);
				File file = createExportFile(param, idx);
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Task Cancel");
				}
				handler.populate(file, datas);
				files.add(file);
				datas.clear();
				idx++;
				lastId = (Long) domainList.get(domainList.size() - 1).get(Constant.ID);
				if (lastId == null || domainList.size() < PAGE_SIZE) {
					break;
				}
			} while (true);
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Task Cancel");
			}
			handler.merge(createExportFile(param, null), files);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Task Cancel");
			} else {
				throw e;
			}
		} finally {
			for (File file : files) {
				if (file != null && file.exists()) {
					file.delete();
				}
			}
		}
	}

	private String[] buildExportFields(List<String> exportFields, Map<String, TemplateField> fieldInfoMap) {
		List<String> fields = new ArrayList<>();
		for (String exportField : exportFields) {
			TemplateField templateField = fieldInfoMap.get(exportField);
			if (templateField == null) {
				continue;
			}
			fields.add(exportField);
		}
		String[] fieldArray = new String[fields.size()];
		fields.toArray(fieldArray);
		return fieldArray;
	}
	
	private List<Object> buildHeads(String[] exportFields, Map<String, TemplateField> fieldInfoMap) {
		List<Object> heads = new ArrayList<>();
		for (int i = 0; i < exportFields.length; i++) {
			String exportField = exportFields[i];
			heads.add(fieldInfoMap.get(exportField).getFieldTitle());
		}
		return heads;
	}
	
	private void buildDatas(List<Map<String, Object>> domainList, List<List<Object>> datas, String[] exportFields) {
		for (Map<String, Object> domainMap : domainList) {
			datas.add(buildData(exportFields, domainMap));
		}
	}

	private List<Object> buildData(String[] exportFields, Map<String, Object> domainMap) {
		List<Object> data = new ArrayList<>();
		for (int i = 0; i < exportFields.length; i++) {
			String exportField = exportFields[i];
			Object value = domainMap.get(exportField);
			data.add(value == null ? "" : value);
		}
		return data;
	}
	
	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
