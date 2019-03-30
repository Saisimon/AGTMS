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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;

/**
 * 导出执行器
 * 
 * @author saisimon
 *
 */
@Component
public class ExportActuator implements Actuator<ExportParam> {
	
	private static final Sign EXPORT_SIGN = Sign.builder().name(Functions.EXPORT.getFunction()).text(Functions.EXPORT.getFunction()).build();
	private static final int PAGE_SIZE = 2000;
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Result execute(ExportParam param) throws Exception {
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		List<Object> heads = new ArrayList<>();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		List<String> fields = new ArrayList<>();
		for (String exportField : param.getExportFields()) {
			TemplateField templateField = fieldInfoMap.get(exportField);
			if (templateField == null) {
				continue;
			}
			heads.add(templateField.getFieldTitle());
			fields.add(exportField);
		}
		String name = UUID.randomUUID().toString();
		File file = createExportFile(param, name);
		fillHead(file, heads, param.getExportFileType(), false);
		GenerateService generateService = GenerateServiceFactory.build(template);
		FilterRequest filter = FilterRequest.build(param.getFilter(), TemplateUtils.getFilters(template)).and(Constant.OPERATORID, param.getUserId());
		Long count = generateService.count(filter);
		long pageCount = (count - 1) / PAGE_SIZE + 1;
		List<List<Object>> datas = new ArrayList<>();
		for (int page = 0; page < pageCount; page++) {
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Task Cancel");
			}
			FilterPageable pageable = new FilterPageable(page, PAGE_SIZE, null);
			List<Domain> domains = generateService.findList(filter, pageable, fields.toArray(new String[fields.size()]));
			List<Map<String, Object>> domainList = SelectionUtils.handleSelection(fieldInfoMap, template.getService(), domains, param.getUserId());
			for (Map<String, Object> domainMap : domainList) {
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Task Cancel");
				}
				List<Object> data = new ArrayList<>();
				for (int i = 0; i < fields.size(); i++) {
					String field = fields.get(i);
					Object value = domainMap.get(field);
					data.add(value == null ? "" : value);
				}
				datas.add(data);
			}
			fillDatas(file, datas, param.getExportFileType(), true);
			datas.clear();
		}
		param.setExportFileUUID(name);
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	public String taskContent(Task task) {
		if (task == null) {
			return null;
		}
		ExportParam param = SystemUtils.fromJson(task.getTaskParam(), ExportParam.class);
		return param.getExportFileName();
	}
	
	@Override
	public void download(Task task, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (task == null || task.getHandleStatus() != HandleStatuses.SUCCESS.getStatus()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		ExportParam param = SystemUtils.fromJson(task.getTaskParam(), ExportParam.class);
		if (param.getExportFileUUID() == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		String userAgent = request.getHeader("user-agent");
		String path = Constant.File.EXPORT_PATH + File.separatorChar + param.getUserId();
		File file = null;
		String filename = param.getExportFileName();
		switch (param.getExportFileType()) {
			case Constant.File.XLS:
				response.setContentType("application/vnd.ms-excel");
				file = new File(path + File.separatorChar + param.getExportFileUUID() + Constant.File.XLS_SUFFIX);
				filename += Constant.File.XLS_SUFFIX;
				break;
			case Constant.File.CSV:
				response.setContentType("application/CSV");
				file = new File(path + File.separatorChar + param.getExportFileUUID() + Constant.File.CSV_SUFFIX);
				filename += Constant.File.CSV_SUFFIX;
				break;
			case Constant.File.XLSX:
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				file = new File(path + File.separatorChar + param.getExportFileUUID() + Constant.File.XLSX_SUFFIX);
				filename += Constant.File.XLSX_SUFFIX;
				break;
			default:
				break;
		}
		if (file == null || !file.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			response.setHeader("Content-Disposition", SystemUtils.encodeDownloadContentDisposition(userAgent, filename));
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
		}
	}
	
	@Override
	public Sign sign() {
		return EXPORT_SIGN;
	}
	
	private File createExportFile(ExportParam param, String name) throws IOException {
		String path = Constant.File.EXPORT_PATH + File.separatorChar + param.getUserId();
		return FileUtils.createFile(path, name, "." + param.getExportFileType());
	}
	
	private void fillHead(File file, List<Object> heads, String fileType, boolean append) throws IOException {
		List<List<Object>> datas = new ArrayList<>();
		datas.add(heads);
		fillDatas(file, datas, fileType, append);
	}
	
	private void fillDatas(File file, List<List<Object>> datas, String fileType, boolean append) throws IOException {
		switch (fileType) {
			case Constant.File.XLS:
				FileUtils.toXLS(file, datas, append);
				break;
			case Constant.File.CSV:
				FileUtils.toCSV(file, datas, ",", append);
				break;
			case Constant.File.XLSX:
				FileUtils.toXLSX(file, datas, append);
				break;
			default:
				break;
		}
	}
	
}
