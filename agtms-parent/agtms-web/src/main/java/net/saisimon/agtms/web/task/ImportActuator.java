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
import net.saisimon.agtms.core.factory.FileHandlerFactory;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ImportParam;
import net.saisimon.agtms.web.util.FileUtils;

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
		if (param == null || param.getUuid() == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		Template template = TemplateUtils.getTemplate(param.getTemplateId(), param.getUserId());
		if (template == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		File file = createImportFile(param);
		if (file == null || !file.exists()) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			response.setContentType(FileUtils.CONTENT_TYPE_MAP.get(param.getImportFileType()));
			response.setHeader("Content-Disposition", SystemUtils.encodeDownloadContentDisposition(request.getHeader("user-agent"), param.getImportFileName() + "." + param.getImportFileType()));
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
			.append(File.separatorChar).append(param.getUuid()).append('.').append(param.getImportFileType());
		File file = new File(importFilePath.toString());
		return file;
	}

	private void importDatas(ImportParam param, Template template, File file) throws InterruptedException, GenerateException, IOException {
		FileHandler handler = FileHandlerFactory.getHandler(param.getImportFileType());
		if (handler == null) {
			return;
		}
		List<List<String>> datas = handler.fetch(file);
		List<List<Object>> resultDatas = new ArrayList<>(datas.size());
		if (datas.size() == 0) {
			handler.populate(file, resultDatas);
			return;
		}
		resultDatas.add(buildHead(datas));
		if (datas.size() == 1) {
			handler.populate(file, resultDatas);
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
		handler.populate(file, resultDatas);
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
	
	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
