package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterParam;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.spring.SpringContext;

/**
 * 下拉列表相关工具类
 * 
 * @author saisimon
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectionUtils {
	
	private static final int OPTION_SIZE = 30;
	
	/**
	 * 获取下拉列表对象
	 * 
	 * @param key 下拉列表唯一标识
	 * @param operatorIds 用户ID集合
	 * @return 下拉列表对象
	 */
	public static Selection getSelection(Object key, Collection<Long> operatorIds) {
		if (key == null || CollectionUtils.isEmpty(operatorIds)) {
			return null;
		}
		String sign = key.toString();
		if (!NumberUtil.isLong(sign)) {
			return getRemoteSelection(sign);
		}
		Optional<Selection> optional = SelectionServiceFactory.get().findById(Long.valueOf(sign));
		if (!optional.isPresent()) {
			return null;
		}
		Selection selection = optional.get();
		if (operatorIds.contains(selection.getOperatorId())) {
			return selection;
		}
		return null;
	}
	
	/**
	 * 获取远程下拉列表对象
	 * 
	 * @param key 下拉列表唯一标识
	 * @return 下拉列表对象
	 */
	public static Selection getRemoteSelection(String key) {
		RemoteService remoteService = SpringContext.getBean("remoteService", RemoteService.class);
		if (remoteService == null) {
			return null;
		}
		String[] strs = key.split("-");
		if (strs.length != 2) {
			return null;
		}
		Selection selection = new Selection();
		selection.setService(strs[0]);
		selection.setKey(strs[1]);
		selection.setType(SelectTypes.REMOTE.getType());
		return selection;
	}
	
	/**
	 * 根据下拉列表标识与下拉列表选项值集合，查询下拉列表的选项映射
	 * 
	 * @param sign 下拉列表标识
	 * @param operatorId 用户ID
	 * @param values 下拉列表选项值集合
	 * @return 下拉列表的选项映射，key为选项值，value为选项名称
	 */
	public static Map<String, String> getSelectionValueTextMap(String sign, Set<String> values, Collection<Long> operatorIds) {
		return getSelectionMap(sign, values, true, operatorIds);
	}
	
	/**
	 * 根据下拉列表标识与下拉列表选项名称集合，查询下拉列表的选项映射
	 * 
	 * @param sign 下拉列表标识
	 * @param operatorId 用户ID
	 * @param texts 下拉列表选项名称集合
	 * @return 下拉列表的选项映射，key为选项名称，value为选项值
	 */
	public static Map<String, String> getSelectionTextValueMap(String sign, Set<String> texts, Collection<Long> operatorIds) {
		return getSelectionMap(sign, texts, false, operatorIds);
	}
	
	/**
	 * 根据下拉列表标识与下拉列表选项名称关键词，查询下拉列表的选项列表
	 * 
	 * @param sign 下拉列表标识
	 * @param keyword 下拉列表选项名称关键词
	 * @param operatorId 用户ID
	 * @return
	 */
	public static List<Option<Object>> getSelectionOptions(String sign, String keyword, Collection<Long> operatorIds) {
		List<Option<Object>> options = new ArrayList<>();
		Selection selection = getSelection(sign, operatorIds);
		if (selection == null) {
			return options;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			List<SelectionOption> selectionOptions = selectionService.searchSelectionOptions(selection, keyword, OPTION_SIZE);
			for (SelectionOption selectionOption : selectionOptions) {
				Option<Object> option = new Option<>(selectionOption.getValue(), selectionOption.getText());
				options.add(option);
			}
		} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
			SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection);
			Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), operatorIds);
			if (template == null) {
				return options;
			}
			FilterRequest filter = FilterRequest.build().and(selectionTemplate.getValueFieldName(), "", Operator.EXISTS);
			if (SystemUtils.isBlank(keyword)) {
				filter.and(selectionTemplate.getTextFieldName(), "", Operator.EXISTS);
			} else {
				filter.and(selectionTemplate.getTextFieldName(), keyword, Operator.REGEX);
			}
			FilterPageable pageable = new FilterPageable(new FilterParam(Constant.ID, null, Operator.LT), OPTION_SIZE, null);
			List<Domain> domains = GenerateServiceFactory.build(template).findList(filter, pageable, selectionTemplate.getValueFieldName(), selectionTemplate.getTextFieldName());
			for (Domain domain : domains) {
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
	
	private static Map<String, String> getSelectionMap(String sign, Set<String> values, boolean valueKey, Collection<Long> operatorIds) {
		Map<String, String> selectionMap = MapUtil.newHashMap();
		if (CollectionUtils.isEmpty(values)) {
			return selectionMap;
		}
		Selection selection = getSelection(sign, operatorIds);
		if (selection == null) {
			return selectionMap;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			List<SelectionOption> selectionOptions = selectionService.getSelectionOptions(selection, values, valueKey);
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
			SelectionTemplate selectionTemplate = selectionService.getSelectionTemplate(selection);
			Template template = TemplateUtils.getTemplate(selectionTemplate.getTemplateId(), operatorIds);
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
