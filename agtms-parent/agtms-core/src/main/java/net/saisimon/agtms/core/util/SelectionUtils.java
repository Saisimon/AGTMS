package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.spring.SpringContext;

/**
 * 下拉列表相关工具类
 * 
 * @author saisimon
 *
 */
public class SelectionUtils {
	
	private static final int OPTION_SIZE = 30;
	
	private SelectionUtils() {}
	
	/**
	 * 获取下拉列表对象
	 * 
	 * @param key 下拉列表唯一标识
	 * @param operatorId 用户ID
	 * @return 下拉列表对象
	 */
	public static Selection getSelection(Object key, Long operatorId) {
		if (key == null || operatorId == null) {
			return null;
		}
		String sign = key.toString();
		if (!NumberUtil.isLong(sign)) {
			RemoteService remoteService = SpringContext.getBean("remoteService", RemoteService.class);
			if (remoteService == null) {
				return null;
			}
			String[] strs = sign.split("-");
			if (strs.length != 2) {
				return null;
			}
			Selection selection = new Selection();
			selection.setService(strs[0]);
			selection.setKey(strs[1]);
			selection.setType(SelectTypes.REMOTE.getType());
			return selection;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		Optional<Selection> optional = selectionService.findById(Long.valueOf(sign));
		if (!optional.isPresent()) {
			return null;
		}
		Selection selection = optional.get();
		UserToken userToken = TokenFactory.get().getToken(operatorId, false);
		if (userToken != null && userToken.isAdmin()) {
			return selection;
		}
		if (operatorId.equals(selection.getOperatorId())) {
			return selection;
		}
		return null;
	}
	
	/**
	 * 处理自定义对象集合中的下拉列表属性
	 * 
	 * @param fieldInfoMap 自定义属性与模板的映射
	 * @param service 服务名称
	 * @param domains 自定义对象集合
	 * @param operatorId 用户ID
	 * @return 处理后的自定义对象集合
	 */
	public static List<Map<String, Object>> handleSelection(Map<String, TemplateField> fieldInfoMap, String service, List<Domain> domains, Long operatorId) {
		List<Map<String, Object>> datas = new ArrayList<>(domains.size());
		Map<String, Set<String>> valueMap = MapUtil.newHashMap();
		for (Domain domain : domains) {
			Map<String, Object> data = new HashMap<>();
			for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
				String fieldName = entry.getKey();
				TemplateField templateField = entry.getValue();
				Object value = domain.getField(fieldName);
				if (value == null) {
					continue;
				}
				if (Views.SELECTION.getView().equals(templateField.getViews())) {
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
		Map<String, Map<String, String>> map = MapUtil.newHashMap();
		for (Map.Entry<String, Set<String>> entry : valueMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = fieldInfoMap.get(fieldName);
			Map<String, String> textMap = getSelectionValueTextMap(templateField.selectionSign(service), operatorId, entry.getValue());
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
	
	/**
	 * 根据下拉列表标识与下拉列表选项值集合，查询下拉列表的选项映射
	 * 
	 * @param sign 下拉列表标识
	 * @param operatorId 用户ID
	 * @param values 下拉列表选项值集合
	 * @return 下拉列表的选项映射，key为选项值，value为选项名称
	 */
	public static Map<String, String> getSelectionValueTextMap(String sign, Long operatorId, Set<String> values) {
		return getSelectionMap(sign, operatorId, values, true);
	}
	
	/**
	 * 根据下拉列表标识与下拉列表选项名称集合，查询下拉列表的选项映射
	 * 
	 * @param sign 下拉列表标识
	 * @param operatorId 用户ID
	 * @param texts 下拉列表选项名称集合
	 * @return 下拉列表的选项映射，key为选项名称，value为选项值
	 */
	public static Map<String, String> getSelectionTextValueMap(String sign, Long operatorId, Set<String> texts) {
		return getSelectionMap(sign, operatorId, texts, false);
	}
	
	/**
	 * 根据下拉列表标识与下拉列表选项名称关键词，查询下拉列表的选项列表
	 * 
	 * @param sign 下拉列表标识
	 * @param keyword 下拉列表选项名称关键词
	 * @param operatorId 用户ID
	 * @return
	 */
	public static List<Option<Object>> getSelectionOptions(String sign, String keyword, Long operatorId) {
		List<Option<Object>> options = new ArrayList<>();
		Selection selection = getSelection(sign, operatorId);
		if (selection == null) {
			return options;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			List<SelectionOption> selectionOptions = selectionService.searchSelectionOptions(selection.getId(), operatorId, keyword, OPTION_SIZE);
			for (SelectionOption selectionOption : selectionOptions) {
				Option<Object> option = new Option<>(selectionOption.getValue(), selectionOption.getText());
				options.add(option);
			}
		} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
			SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection.getId(), operatorId);
			Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), operatorId);
			if (template == null) {
				return options;
			}
			FilterRequest filter = FilterRequest.build().and(selectionTemplate.getValueFieldName(), "", Operator.EXISTS);
			if (SystemUtils.isBlank(keyword)) {
				filter.and(selectionTemplate.getTextFieldName(), "", Operator.EXISTS);
			} else {
				filter.and(selectionTemplate.getTextFieldName(), keyword, Operator.REGEX);
			}
			FilterPageable pageable = new FilterPageable(0, OPTION_SIZE, null);
			Page<Domain> page = GenerateServiceFactory.build(template).findPage(filter, pageable, selectionTemplate.getValueFieldName(), selectionTemplate.getTextFieldName());
			for (Domain domain : page.getContent()) {
				String text = domain.getField(selectionTemplate.getTextFieldName()).toString();
				Option<Object> option = new Option<>(domain.getField(selectionTemplate.getValueFieldName()), text);
				options.add(option);
			}
		} else if (SelectTypes.REMOTE.getType().equals(selection.getType())) {
			RemoteService remoteService = SpringContext.getBean("remoteService", RemoteService.class);
			if (remoteService == null) {
				return options;
			}
			Map<String, Object> body = MapUtil.newHashMap();
			body.put("text", keyword);
			LinkedHashMap<?, String> selectionMap = remoteService.selection(selection.getService(), selection.getKey(), body);
			for (Map.Entry<?, String> entry : selectionMap.entrySet()) {
				Option<Object> option = new Option<>(entry.getKey(), entry.getValue());
				options.add(option);
			}
		}
		return options;
	}
	
	private static Map<String, String> getSelectionMap(String sign, Long operatorId, Set<String> values, boolean valueKey) {
		Map<String, String> selectionMap = MapUtil.newHashMap();
		if (CollectionUtils.isEmpty(values)) {
			return selectionMap;
		}
		Selection selection = getSelection(sign, operatorId);
		if (selection == null) {
			return selectionMap;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			List<SelectionOption> selectionOptions = selectionService.getSelectionOptions(selection.getId(), operatorId, values, valueKey);
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
		} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
			SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection.getId(), operatorId);
			Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), operatorId);
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
			List<Domain> domains = GenerateServiceFactory.build(template).findList(filter, (FilterSort)null, selectionTemplate.getValueFieldName(), selectionTemplate.getTextFieldName());
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
		} else if (SelectTypes.REMOTE.getType().equals(selection.getType())) {
			RemoteService remoteService = SpringContext.getBean("remoteService", RemoteService.class);
			if (remoteService == null) {
				return selectionMap;
			}
			Map<String, Object> body = MapUtil.newHashMap();
			if (valueKey) {
				body.put("values", values);
			} else {
				body.put("texts", values);
			}
			LinkedHashMap<?, String> map = remoteService.selection(selection.getService(), selection.getKey(), body);
			for (Map.Entry<?, String> entry : map.entrySet()) {
				selectionMap.put(entry.getKey().toString(), entry.getValue());
			}
		}
		return selectionMap;
	}
	
}
