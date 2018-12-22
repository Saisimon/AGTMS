package net.saisimon.agtms.web.config.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.concurrent.DoSomethingByPageTask;
import net.saisimon.agtms.core.constant.FileConstant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.spring.SpringContext;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

@Slf4j
public class BatchImportHandler extends BasicWebSocketHandler {
	
	private Template template;
	private String filename;
	private long size = -1;
	private int totalCount;
	private AtomicInteger currentCount;
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String json = new String(message.asBytes(), "UTF-8");
		@SuppressWarnings("unchecked")
		Map<String, Object> request = SystemUtils.fromJson(json, Map.class, String.class, Object.class);
		size = getFileSize(request);
		if (size == -1) {
			session.close(CloseStatus.BAD_DATA);
			log.warn("file size wrong");
			return;
		}
		template = getTemplate(request, session);
		if (template == null) {
			session.close(CloseStatus.BAD_DATA);
			log.warn("management not found");
			return;
		}
		String type = acceptType(request);
		if (type == null) {
			session.close(CloseStatus.BAD_DATA);
			log.warn("type not accept");
			return;
		}
		switch (type) {
		case FileConstant.XLS:
			filename = FileConstant.IMPORT_PATH + File.separator + template.getId() + FileConstant.XLS_SUFFIX;
			break;
		case FileConstant.XLSX:
			filename = FileConstant.IMPORT_PATH + File.separator + template.getId() + FileConstant.XLSX_SUFFIX;
			break;
		default:
			session.close(CloseStatus.BAD_DATA);
			log.warn("type wrong");
			return;
		}
		File file = new File(filename);
		FileUtils.createDir(file);
		try {
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			log.error("delete file error, filename: " + filename, e);
		}
		session.sendMessage(new TextMessage("OK"));
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
		if (!checkParams()) {
			session.close(CloseStatus.BAD_DATA);
			return;
		}
		File file = new File(filename);
		try {
			if (!importFile(file, message)) {
				session.close(CloseStatus.BAD_DATA);
				log.warn("import file too large");
				return;
			}
			session.sendMessage(new TextMessage("OK"));
		} catch (IOException e) {
			session.close(CloseStatus.SERVER_ERROR);
			log.error("write file error", e);
			return;
		}
		if (file.length() == size) {
			Map<String, List<List<String>>> dataMap = parseFile(file);
			Map<String, List<String>> resultMap = importDatas(dataMap, session);
			session.sendMessage(new TextMessage(SystemUtils.toJson(resultMap)));
		}
	}
	
	private boolean checkParams() {
		if (StringUtils.isBlank(filename)) {
			log.warn("filename wrong");
			return false;
		}
		if (size == -1) {
			log.warn("file size wrong");
			return false;
		}
		if (template == null) {
			log.warn("template not found");
			return false;
		}
		return true;
	}
	
	private boolean importFile(File file, BinaryMessage message) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file, true)) {
			FileChannel fc = out.getChannel();
			if (fc.size() + message.getPayloadLength() > FileConstant.MAX_IMPORT_SIZE) {
				return false;
			}
			fc.position(fc.size());
			ByteBuffer buffer = message.getPayload();
			while (buffer.hasRemaining()) {
				fc.write(buffer);
			}
			return true;
		}
	}

	private Map<String, List<String>> importDatas(Map<String, List<List<String>>> dataMap, WebSocketSession session) {
		Map<String, List<String>> resultMap = new LinkedHashMap<>();
		if (dataMap.size() > 0) {
			totalCount = calcTotalCount(dataMap);
			currentCount = new AtomicInteger();
			log.info("Total Count: " + totalCount);
			ForkJoinPool pool = new ForkJoinPool();
			for (Entry<String, List<List<String>>> entry : dataMap.entrySet()) {
				List<List<String>> datas = entry.getValue();
				if (datas.size() < 2) {
					continue;
				}
				Map<Integer, String> indexMap = buildIndexMap(datas.get(0), template);
				if (indexMap == null) {
					continue;
				}
				DomainHandler handler = new DomainHandler(1, datas.size() - 1, -1, datas, indexMap, session);
				handler = SpringContext.autowire(handler);
				pool.execute(handler);
				while (!handler.isDone()) {
					DomainHandler.printLog(pool);
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						log.error("wait task done error", e);
					}
				}
				resultMap.put(entry.getKey(), handler.getRawResult());
			}
			pool.shutdown();
			try {
				pool.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				log.error("await termination error", e);
			}
			DomainHandler.printLog(pool);
		}
		return resultMap;
	}

	private Map<String, List<List<String>>> parseFile(File file) {
		Map<String, List<List<String>>> dataMap = null;
		try (InputStream in = new FileInputStream(file)) {
			if (filename.endsWith(FileConstant.XLS_SUFFIX)) {
				dataMap = FileUtils.fromXLS(in);
			} else {
				dataMap = FileUtils.fromXLSX(in);
			}
		} catch (IOException e) {
			log.error("parse file error", e);
		}
		return dataMap;
	}
	
	private int calcTotalCount(Map<String, List<List<String>>> dataMap) {
		int count = 0;
		for (List<List<String>> datas : dataMap.values()) {
			count += (datas.size() - 1);
		}
		return count;
	}
	
	private String acceptType(Map<String, Object> request) {
		Object typeObj = request.get("type");
		if (typeObj == null) {
			return null;
		}
		String type = typeObj.toString().trim().toLowerCase();
		if (type.equals(FileConstant.XLS) || type.equals(FileConstant.XLSX)) {
			return type;
		}
		return null;
	}
	
	private long getFileSize(Map<String, Object> request) {
		Object sizeObj = request.get("size");
		if (sizeObj == null) {
			return -1;
		}
		try {
			return Long.parseLong(sizeObj.toString());
		} catch (NumberFormatException e) {
			log.error("parse file size error", e);
			return -1;
		}
	}
	
	private Map<Integer, String> buildIndexMap(List<String> heads, Template template) {
		Map<Integer, String> indexMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(heads)) {
			Map<String, String> headMap = TemplateUtils.getHeadMap(template);
			for (Entry<String, String> entry : headMap.entrySet()) {
				int idx = heads.indexOf(entry.getKey());
				if (idx != -1) {
					indexMap.put(idx, entry.getValue());
				}
			}
		}
		return indexMap;
	}
	
	class DomainHandler extends DoSomethingByPageTask<List<String>> {

		private static final long serialVersionUID = 1L;
		
		@Autowired
		private MessageSource messageSource;
		
		private List<List<String>> datas;
		private Map<Integer, String> indexMap;
		private WebSocketSession session;

		public DomainHandler(long start, long end, int pageNumber, 
				List<List<String>> datas, 
				Map<Integer, String> indexMap, 
				WebSocketSession session) {
			super(start, end, pageNumber);
			this.datas = datas;
			this.indexMap = indexMap;
			this.session = session;
		}

		@Override
		protected List<String> doSomething() {
			try {
				int e = (int) end;
				if (e >= datas.size()) {
					e = datas.size() - 1;
				}
				List<List<String>> subDatas = datas.subList((int)start, e + 1);
				return saveDomains(subDatas, indexMap, template);
			} catch (Exception e) {
				log.error("save domain error", e);
			}
			return null;
		}

		@Override
		protected DoSomethingByPageTask<List<String>> build(long start, long end, int pageNumber, int pageSize) {
			DomainHandler handler = new DomainHandler(start, end, pageNumber, datas, indexMap, session);
			handler = SpringContext.autowire(handler);
			return handler;
		}

		@Override
		protected List<String> joinResult(List<List<String>> results) {
			List<String> rs = new ArrayList<>();
			if (!CollectionUtils.isEmpty(results)) {
				for (List<String> result : results) {
					rs.addAll(result);
				}
			}
			return rs;
		}
		
		private List<String> saveDomains(List<List<String>> datas, 
				Map<Integer, String> indexMap, 
				Template template) throws GenerateException, IOException {
			GenerateService generateService = GenerateServiceFactory.build(template);
			List<String> saveResults = new ArrayList<>(datas.size() - 1);
			Set<String> requireds = TemplateUtils.getRequireds(template);
			Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
			for (int i = 0; i < datas.size(); i++) {
				String result;
				List<String> data = datas.get(i);
				Domain domain = tranform(data, indexMap, template, generateService);
				if (domain == null) {
					result = "<span class='text-danger'>" + getMessage("tranform.failed.data", (Object[])null) + "</span>";
				} else {
					List<String> blankFieldNames = checkRequired(domain, requireds);
					if (blankFieldNames.size() > 0) {
						result = "<span class='text-danger'>" + getMessage("missing.fields.data", blankFieldNames.stream().map(blankFieldName -> {
							return fieldInfoMap.get(blankFieldName).getFieldTitle();
						}).collect(Collectors.toList())) + "</span>";
					} else if (generateService.checkExist(domain)) {
						result = "<span class='text-danger'>" + getMessage("not.unique.data", blankFieldNames.stream().map(blankFieldName -> {
							return fieldInfoMap.get(blankFieldName).getFieldTitle();
						}).collect(Collectors.toList())) + "</span>";
					} else if (generateService.saveDomain(domain)) {
						result = "<span class='text-success'>" + getMessage("save.success.data", (Object[])null) + "</span>";
					} else {
						result = "<span class='text-danger'>" + getMessage("save.failed.data", (Object[])null) + "</span>";
					}
				}
				saveResults.add(result);
				synchronized (session) {
					session.sendMessage(new TextMessage(String.format("%.2f", currentCount.incrementAndGet() * 100.0 / totalCount)));
				}
			}
			return saveResults;
		}
		
		private Domain tranform(List<String> data, 
				Map<Integer, String> indexMap, 
				Template template,
				GenerateService generateService) {
			try {
				Domain domain = generateService.newGenerate();
				Map<String, String> fieldMap = TemplateUtils.buildFieldMap(template);
				for (Entry<Integer, String> entry : indexMap.entrySet()) {
					Integer idx = entry.getKey();
					String fieldName = entry.getValue();
					if (idx < data.size()) {
						try {
							Object fieldValue = SystemUtils.parseFieldValue(data.get(idx), fieldMap.get(fieldName));
							domain.setField(fieldName, fieldValue, fieldValue.getClass());
						} catch (ClassNotFoundException e) {
							log.error("field class not found", e);
						}
					}
				}
				domain.setField("creator", template.getUserId(), Long.class);
				return domain;
			} catch (Exception e) {
				log.error("transform data failed", e);
				return null;
			}
		}
		
		private List<String> checkRequired(Domain domain, Set<String> requireds) {
			List<String> blankFieldNames = new ArrayList<>();
			for (String fieldName : requireds) {
				Object val = domain.getField(fieldName);
				if (val == null || StringUtils.isBlank(val.toString())) {
					blankFieldNames.add(fieldName);
				}
			}
			return blankFieldNames;
		}
		
		private String getMessage(String code, Object... args) {
			return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
		}
		
	}
	
}
