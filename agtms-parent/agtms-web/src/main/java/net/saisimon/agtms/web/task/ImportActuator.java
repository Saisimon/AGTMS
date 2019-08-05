package net.saisimon.agtms.web.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
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
	@Autowired
	private AgtmsProperties agtmsProperties;
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Result execute(ImportParam param) throws Exception {
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.IMPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		File file = createImportFile(param);
		if (!file.exists()) {
			return ErrorMessage.Task.Import.TASK_IMPORT_FAILED;
		}
		importDatas(param, template, file);
		return ResultUtils.simpleSuccess();
	}

	@Override
	public String taskContent(ImportParam param) {
		return param == null ? null : param.getImportFileName();
	}
	
	@Override
	public String handleResult(String handleResult) {
		if (SystemUtils.isBlank(handleResult)) {
			return handleResult;
		}
		if (handleResult.equals(ErrorMessage.Task.Import.TASK_IMPORT_MAX_SIZE_LIMIT.getMessage())) {
			return getMessage(ErrorMessage.Task.Import.TASK_IMPORT_MAX_SIZE_LIMIT.getMessage(), agtmsProperties.getImportRowsMaxSize());
		} else {
			return getMessage(handleResult);
		}
	}
	
	@Override
	public void download(ImportParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (param == null || param.getImportFileUUID() == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		StringBuilder importFilePath = new StringBuilder();
		importFilePath.append(agtmsProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.IMPORT_PATH)
			.append(File.separatorChar).append(param.getUserId())
			.append(File.separatorChar).append(param.getImportFileUUID());
		File file = null;
		String filename = param.getImportFileName();
		switch (param.getImportFileType()) {
			case Constant.File.XLS:
				response.setContentType("application/vnd.ms-excel");
				importFilePath.append(Constant.File.XLS_SUFFIX);
				file = new File(importFilePath.toString());
				filename += Constant.File.XLS_SUFFIX;
				break;
			case Constant.File.CSV:
				response.setContentType("application/CSV");
				importFilePath.append(Constant.File.CSV_SUFFIX);
				file = new File(importFilePath.toString());
				filename += Constant.File.CSV_SUFFIX;
				break;
			case Constant.File.XLSX:
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				importFilePath.append(Constant.File.XLSX_SUFFIX);
				file = new File(importFilePath.toString());
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
			response.setHeader("Content-Disposition", SystemUtils.encodeDownloadContentDisposition(request.getHeader("user-agent"), filename));
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
		}
	}
	
	@Override
	public void delete(ImportParam param) throws Exception {
		if (param == null) {
			return;
		}
		File file = createImportFile(param);
		if (file == null || !file.exists()) {
			return;
		}
		file.delete();
	}

	@Override
	public Sign sign() {
		return IMPORT_SIGN;
	}
	
	private File createImportFile(ImportParam param) {
		StringBuilder importFilePath = new StringBuilder();
		importFilePath.append(agtmsProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.IMPORT_PATH)
			.append(File.separatorChar).append(param.getUserId())
			.append(File.separatorChar).append(param.getImportFileUUID()).append('.').append(param.getImportFileType());
		File file = new File(importFilePath.toString());
		return file;
	}

	private void importDatas(ImportParam param, Template template, File file) throws InterruptedException, GenerateException, IOException {
		List<List<String>> datas = parseFile(file, param.getImportFileType());
		List<List<Object>> resultDatas = new ArrayList<>(datas.size());
		if (datas.size() == 0) {
			fillDatas(file, resultDatas, param.getImportFileType());
			return;
		}
		resultDatas.add(buildHead(datas));
		if (datas.size() == 1) {
			fillDatas(file, resultDatas, param.getImportFileType());
			return;
		}
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		Map<String, Map<String, String>> fieldTextMap = new HashMap<>();
		for (int i = 1; i < datas.size(); i++) {
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException("Task Cancel");
			}
			List<Object> resultData = importData(param, template, datas.get(i), fieldInfoMap, fieldTextMap);
			resultDatas.add(resultData);
		}
		fillDatas(file, resultDatas, param.getImportFileType());
	}

	private List<Object> importData(ImportParam param, Template template, List<String> data, 
			Map<String, TemplateField> fieldInfoMap, Map<String, Map<String, String>> fieldTextMap) throws GenerateException {
		Domain domain = GenerateServiceFactory.build(template).newGenerate();
		List<Object> resultData = new ArrayList<>();
		List<String> missRequireds = new ArrayList<>();
		for (int j = 0; j < param.getImportFields().size(); j++) {
			if (j < data.size()) {
				String fieldName = param.getImportFields().get(j);
				Object fieldValue = data.get(j);
				TemplateField templateField = fieldInfoMap.get(fieldName);
				if (fieldValue != null && Views.SELECTION.getView().equals(templateField.getViews())) {
					Map<String, String> textMap = fieldTextMap.get(fieldName);
					if (textMap == null) {
						textMap = new HashMap<>();
						fieldTextMap.put(fieldName, textMap);
					}
					String value = textMap.get(fieldValue.toString());
					if (value == null) {
						Set<String> texts = new HashSet<>();
						texts.add(fieldValue.toString());
						Map<String, String> textValueMap = SelectionUtils.getSelectionTextValueMap(templateField.selectionSign(template.getService()), texts, param.getUserId());
						textMap.putAll(textValueMap);
						value = textValueMap.get(fieldValue.toString());
					}
					fieldValue = value;
				}
				if (Views.PASSWORD.getView().equals(templateField.getViews())) {
					fieldValue = DomainUtils.encrypt(fieldValue);
				}
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
		domain.setField(Constant.OPERATORID, param.getUserId(), Long.class);
		if (missRequireds.size() > 0) {
			resultData.add(getMessage("missing.required.field") + ": " + missRequireds.stream().collect(Collectors.joining(", ")));
		} else if (GenerateServiceFactory.build(template).checkExist(domain, param.getUserId())) {
			resultData.add(getMessage("domain.already.exists"));
		} else {
			GenerateServiceFactory.build(template).saveDomain(domain, param.getUserId());
			resultData.add(getMessage("success"));
		}
		return resultData;
	}

	private List<Object> buildHead(List<List<String>> datas) {
		List<String> heads = datas.get(0);
		List<Object> headList = new ArrayList<>(heads.size() + 1);
		for (String head : heads) {
			headList.add(head);
		}
		headList.add(getMessage("result"));
		return headList;
	}
	
	private void fillDatas(File file, List<List<Object>> datas, String fileType) throws IOException {
		switch (fileType) {
			case Constant.File.XLS:
				FileUtils.toXLS(file, datas, false);
				break;
			case Constant.File.CSV:
				FileUtils.toCSV(file, datas, ",", false);
				break;
			case Constant.File.XLSX:
				FileUtils.toXLSX(file, datas, false);
				break;
			default:
				break;
		}
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
					List<List<String>> dataCSVList = FileUtils.fromCSV(in);
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
