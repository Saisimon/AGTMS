package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import cn.hutool.core.date.DateUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.SelectionService;

public class DomainUtils {
	
	private DomainUtils() {
		throw new IllegalAccessError();
	}
	
	public static void fillCommonFields(Domain newDomain, Domain oldDomain) {
		Date time = new Date();
		if (oldDomain == null) {
			newDomain.setField(Constant.CREATETIME, time, Date.class);
			UserInfo userInfo = AuthUtils.getUserInfo();
			if (userInfo != null) {
				newDomain.setField(Constant.OPERATORID, userInfo.getUserId(), Long.class);
			}
		} else {
			newDomain.setField(Constant.ID, oldDomain.getField(Constant.ID), Long.class);
			newDomain.setField(Constant.CREATETIME, oldDomain.getField(Constant.CREATETIME), Date.class);
			newDomain.setField(Constant.OPERATORID, oldDomain.getField(Constant.OPERATORID), Long.class);
		}
		newDomain.setField(Constant.UPDATETIME, time, Date.class);
	}
	
	public static Object parseFieldValue(Object fieldValue, String fieldType) {
		if (fieldValue != null && fieldType != null) {
			try {
				if (Classes.LONG.getName().equals(fieldType)) {
					return Long.valueOf(fieldValue.toString());
				} else if (Classes.DOUBLE.getName().equals(fieldType)) {
					return Double.valueOf(fieldValue.toString());
				} else if (Classes.DATE.getName().equals(fieldType)) {
					return DateUtil.parseDate(fieldValue.toString()).toJdkDate();
				}
			} catch (Exception e) {
				return null;
			}
		}
		return fieldValue;
	}
	
	public static List<Map<String, Object>> handleSelection(Map<String, TemplateField> fieldInfoMap, List<Domain> domains, Long userId) {
		List<Map<String, Object>> datas = new ArrayList<>(domains.size());
		Map<String, Set<String>> valueMap = new HashMap<>();
		for (Domain domain : domains) {
			Map<String, Object> data = new HashMap<>();
			for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
				String fieldName = entry.getKey();
				TemplateField templateField = entry.getValue();
				Object value = domain.getField(fieldName);
				if (value == null) {
					continue;
				}
				if (Views.SELECTION.getView().equals(templateField.getView())) {
					Set<String> values = valueMap.get(fieldName);
					if (values == null) {
						values = new HashSet<>();
						valueMap.put(fieldName, values);
					}
					values.add(value.toString());
				}
				data.put(fieldName, value);
			}
			data.put(Constant.ID, domain.getField(Constant.ID));
			datas.add(data);
		}
		Map<String, Map<String, String>> map = new HashMap<>();
		for (Map.Entry<String, Set<String>> entry : valueMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = fieldInfoMap.get(fieldName);
			Map<String, String> textMap = getSelectionValueTextMap(templateField.getSelectionId(), userId, entry.getValue());
			map.put(fieldName, textMap);
		}
		for (Map<String, Object> data : datas) {
			for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
				String fieldName = entry.getKey();
				Map<String, String> textMap = entry.getValue();
				Object value = data.get(fieldName);
				if (value == null) {
					continue;
				}
				String text = textMap.get(value.toString());
				data.put(fieldName, text);
			}
		}
		return datas;
	}
	
	public static Map<String, String> getSelectionValueTextMap(Long selectionId, Long operatorId, Set<String> values) {
		return getSelectionMap(selectionId, operatorId, values, true);
	}
	
	public static Map<String, String> getSelectionTextValueMap(Long selectionId, Long operatorId, Set<String> texts) {
		return getSelectionMap(selectionId, operatorId, texts, false);
	}
	
	private static Map<String, String> getSelectionMap(Long selectionId, Long operatorId, Set<String> values, boolean valueKey) {
		Map<String, String> selectionMap = new HashMap<>();
		if (CollectionUtils.isEmpty(values)) {
			return selectionMap;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		Selection selection = selectionService.getSelection(selectionId, operatorId);
		if (selection == null) {
			return selectionMap;
		}
		if (SelectTypes.OPTION.getType() == selection.getType()) {
			List<SelectionOption> selectionOptions = null;
			if (valueKey) {
				selectionOptions = selectionService.getSelectionOptions(selectionId, values, null);
			} else {
				selectionOptions = selectionService.getSelectionOptions(selectionId, null, values);
			}
			if (selectionOptions == null) {
				return selectionMap;
			}
			for (SelectionOption selectionOption : selectionOptions) {
				if (valueKey) {
					selectionMap.put(selectionOption.getValue(), selectionOption.getText());
				} else {
					selectionMap.put(selectionOption.getText(), selectionOption.getValue());
				}
			}
		} else if (SelectTypes.TEMPLATE.getType() == selection.getType()) {
			SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection.getId());
			Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), operatorId);
			GenerateService generateService = GenerateServiceFactory.build(template);
			FilterRequest filter = FilterRequest.build();
			if (valueKey) {
				if (values.size() == 1) {
					filter.and(selectionTemplate.getValueFieldName(), values.iterator().next());
				} else {
					filter.and(selectionTemplate.getValueFieldName(), values, Operator.IN);
				}
			} else {
				if (values.size() == 1) {
					filter.and(selectionTemplate.getTextFieldName(), values.iterator().next());
				} else {
					filter.and(selectionTemplate.getTextFieldName(), values, Operator.IN);
				}
			}
			List<Domain> domains = generateService.findList(filter, (FilterSort)null, selectionTemplate.getValueFieldName(), selectionTemplate.getTextFieldName());
			if (CollectionUtils.isEmpty(domains)) {
				return selectionMap;
			}
			for (Domain domain : domains) {
				Object domainValue = domain.getField(selectionTemplate.getValueFieldName());
				Object domainText = domain.getField(selectionTemplate.getTextFieldName());
				if (valueKey) {
					selectionMap.put(domainValue.toString(), domainText.toString());
				} else {
					selectionMap.put(domainText.toString(), domainValue.toString());
				}
			}
		}
		return selectionMap;
	}
	
}
