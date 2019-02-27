package net.saisimon.agtms.web.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Task;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ImportParam;

/**
 * 导入执行器
 * 
 * @author saisimon
 *
 */
@Component
public class ImportActuator implements Actuator<ImportParam> {
	
	private static final Sign IMPORT_SIGN = Sign.builder().name(Functions.IMPORT.getFunction()).text(Functions.IMPORT.getFunction()).build();
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public Result execute(ImportParam param) throws Exception {
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		String path = Constant.File.IMPORT_PATH + File.separatorChar + param.getUserId();
		File file = new File(path + File.separator + param.getImportFileUUID() + "." + param.getImportFileType());
		if (!file.exists()) {
			return ErrorMessage.Task.IMPORT.TASK_IMPORT_FAILED;
		}
		List<List<String>> datas = parseFile(file, param.getImportFileType());
		if (datas.size() < 2) {
			return ResultUtils.simpleSuccess();
		}
		List<String> heads = datas.get(0);
		heads.add(getMessage("result"));
		List<List<Object>> resultDatas = new ArrayList<>(datas.size() - 1);
		GenerateService generateService = GenerateServiceFactory.build(template);
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		for (int i = 1; i < datas.size(); i++) {
			if (Thread.currentThread().isInterrupted()) {
				return ErrorMessage.Task.TASK_CANCEL;
			}
			Domain domain = generateService.newGenerate();
			List<String> data = datas.get(i);
			List<Object> resultData = new ArrayList<>();
			List<String> missRequireds = new ArrayList<>();
			for (int j = 0; j < param.getImportFields().size(); j++) {
				if (j < data.size()) {
					String fieldName = param.getImportFields().get(j);
					Object fieldValue = data.get(j);
					TemplateField templateField = fieldInfoMap.get(fieldName);
					if (fieldValue == null) {
						fieldValue = templateField.getDefaultValue();
					}
					fieldValue = DomainUtils.parseFieldValue(fieldValue, templateField.getFieldType());
					if (templateField.getRequired() && fieldValue == null) {
						missRequireds.add(templateField.getFieldTitle());
						continue;
					}
					if (fieldValue != null) {
						domain.setField(fieldName, fieldValue, fieldValue.getClass());
					}
					resultData.add(data.get(j));
				} else {
					resultData.add("");
				}
			}
			domain.setField(Constant.OPERATORID, template.getOperatorId(), Long.class);
			if (missRequireds.size() > 0) {
				resultData.add(getMessage("missing.required.field") + ": " + missRequireds.stream().collect(Collectors.joining(", ")));
			} else if (generateService.checkExist(domain)) {
				resultData.add(getMessage("domain.already.exists"));
			} else {
				generateService.saveDomain(domain);
				resultData.add(getMessage("success"));
			}
			resultDatas.add(resultData);
		}
		switch (param.getImportFileType()) {
			case Constant.File.XLS:
				file = FileUtils.toXLS(path, param.getImportFileUUID(), heads, resultDatas, null);
				break;
			case Constant.File.CSV:
				file = FileUtils.toCSV(path, param.getImportFileUUID(), heads, resultDatas, ",");
				break;
			case Constant.File.XLSX:
				file = FileUtils.toXLSX(path, param.getImportFileUUID(), heads, resultDatas, null);
				break;
			default:
				break;
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	public String taskContent(Task task) {
		if (task == null) {
			return null;
		}
		ImportParam param = SystemUtils.fromJson(task.getTaskParam(), ImportParam.class);
		return param.getImportFileName();
	}
	
	@Override
	public void download(Task task, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (task == null || task.getHandleStatus() != HandleStatuses.SUCCESS.getStatus()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		ImportParam param = SystemUtils.fromJson(task.getTaskParam(), ImportParam.class);
		if (param.getImportFileUUID() == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		String userAgent = request.getHeader("user-agent");
		String path = Constant.File.IMPORT_PATH + File.separatorChar + param.getUserId();
		File file = null;
		String filename = param.getImportFileName();
		switch (param.getImportFileType()) {
			case Constant.File.XLS:
				response.setContentType("application/vnd.ms-excel");
				file = new File(path + File.separatorChar + param.getImportFileUUID() + Constant.File.XLS_SUFFIX);
				filename += Constant.File.XLS_SUFFIX;
				break;
			case Constant.File.CSV:
				response.setContentType("application/CSV");
				file = new File(path + File.separatorChar + param.getImportFileUUID() + Constant.File.CSV_SUFFIX);
				filename += Constant.File.CSV_SUFFIX;
				break;
			case Constant.File.XLSX:
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				file = new File(path + File.separatorChar + param.getImportFileUUID() + Constant.File.XLSX_SUFFIX);
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
		return IMPORT_SIGN;
	}
	
	private List<List<String>> parseFile(File file, String fileType) throws IOException {
		List<List<String>> datas = new ArrayList<>();
		try (FileInputStream in = new FileInputStream(file)) {
			switch (fileType) {
				case Constant.File.XLS:
					Map<String, List<List<String>>> dataXLSMap = FileUtils.fromXLS(in);
					for (List<List<String>> value : dataXLSMap.values()) {
						if (value.size() > 1) {
							if (datas.isEmpty()) {
								datas.add(value.get(0));
							}
							datas.addAll(value.subList(1, value.size()));
						}
					}
					break;
				case Constant.File.CSV:
					List<List<String>> dataCSVList = FileUtils.fromCSV(in, ",");
					datas.addAll(dataCSVList);
					break;
				case Constant.File.XLSX:
					Map<String, List<List<String>>> dataXLSXMap = FileUtils.fromXLSX(in);
					for (List<List<String>> value : dataXLSXMap.values()) {
						if (value.size() > 1) {
							if (datas.isEmpty()) {
								datas.add(value.get(0));
							}
							datas.addAll(value.subList(1, value.size()));
						}
					}
					break;
				default:
					break;
			}
		}
		return datas;
	}
	
	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
