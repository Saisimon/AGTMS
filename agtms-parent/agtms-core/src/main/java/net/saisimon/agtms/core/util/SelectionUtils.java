package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.SelectionService;

public class SelectionUtils {
	
	public static final int OPTION_SIZE = 30;
	
	private SelectionUtils() {
		throw new IllegalAccessError();
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
	
	public static List<Option<Object>> getSelectionOptions(Long selectionId, String keyword, Long operatorId) {
		List<Option<Object>> options = new ArrayList<>();
		SelectionService selectionService = SelectionServiceFactory.get();
		Selection selection = selectionService.getSelection(selectionId, operatorId);
		if (selection == null) {
			return options;
		}
		if (SelectTypes.OPTION.getType() == selection.getType()) {
			List<SelectionOption> selectionOptions = selectionService.searchSelectionOptions(selection.getId(), keyword, OPTION_SIZE);
			for (SelectionOption selectionOption : selectionOptions) {
				Option<Object> option = new Option<>(selectionOption.getValue(), selectionOption.getText());
				options.add(option);
			}
		} else if (SelectTypes.TEMPLATE.getType() == selection.getType()) {
			SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection.getId());
			Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), operatorId);
			if (template == null) {
				return options;
			}
			GenerateService generateService = GenerateServiceFactory.build(template);
			FilterRequest filter = FilterRequest.build().and(selectionTemplate.getValueFieldName(), "", Operator.EXISTS);
			if (StringUtils.isBlank(keyword)) {
				filter.and(selectionTemplate.getTextFieldName(), "", Operator.EXISTS);
			} else {
				filter.and(selectionTemplate.getTextFieldName(), keyword, Operator.REGEX);
			}
			FilterPageable pageable = new FilterPageable(0, OPTION_SIZE, null);
			Page<Domain> page = generateService.findPage(filter, pageable, selectionTemplate.getValueFieldName(), selectionTemplate.getTextFieldName());
			for (Domain domain : page.getContent()) {
				String text = domain.getField(selectionTemplate.getTextFieldName()).toString();
				Option<Object> option = new Option<>(domain.getField(selectionTemplate.getValueFieldName()), text);
				options.add(option);
			}
		}
		return options;
	}
	
}
