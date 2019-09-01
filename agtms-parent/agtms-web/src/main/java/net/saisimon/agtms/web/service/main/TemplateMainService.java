package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;
import static net.saisimon.agtms.core.constant.Constant.Param.PARAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.TemplateInfo;
import net.saisimon.agtms.web.selection.ResourceSelection;
import net.saisimon.agtms.web.selection.UserSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 模板主服务
 * 
 * @author saisimon
 *
 */
@Service
public class TemplateMainService extends AbstractMainService {
	
	public static final String TEMPLATE = "template";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.CREATE, 
			Functions.EDIT,
			Functions.REMOVE, 
			Functions.BATCH_REMOVE
	);
	
	private static final String TEMPLATE_FILTERS = TEMPLATE + FILTER_SUFFIX;
	private static final String TEMPLATE_PAGEABLE = TEMPLATE + PAGEABLE_SUFFIX;
	private static final Set<String> TEMPLATE_FILTER_FIELDS = new HashSet<>();
	static {
		TEMPLATE_FILTER_FIELDS.add("path");
		TEMPLATE_FILTER_FIELDS.add("title");
		TEMPLATE_FILTER_FIELDS.add("createTime");
		TEMPLATE_FILTER_FIELDS.add("updateTime");
		TEMPLATE_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Autowired
	private ResourceSelection resourceSelection;
	@Autowired
	private UserSelection userSelection;
	@Autowired
	private DomainGenerater domainGenerater;
	@Autowired
	private PremissionService premissionService;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(TEMPLATE));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, TEMPLATE_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, premissionService.getUserIds(AuthUtils.getUid()), Constant.Operator.IN);
		if (pageableMap != null) {
			pageableMap.remove(PARAM);
		}
		FilterPageable pageable = FilterPageable.build(pageableMap);
		TemplateService templateService = TemplateServiceFactory.get();
		List<Template> list = templateService.findPage(filter, pageable, false).getContent();
		List<TemplateInfo> results = new ArrayList<>(list.size());
		Map<String, String> resourceMap = resourceSelection.select();
		Map<String, String> userMap = userSelection.select();
		for (Template template : list) {
			TemplateInfo result = buildTemplateInfo(template);
			String navigation = resourceMap.get(template.getPath());
			if (SystemUtils.isBlank(navigation)) {
				navigation = "/";
			}
			result.setNavigationName(navigation);
			result.setOperator(userMap.get(template.getOperatorId().toString()));
			result.setAction(TEMPLATE);
			results.add(result);
		}
		request.getSession().setAttribute(TEMPLATE_FILTERS, filterMap);
		request.getSession().setAttribute(TEMPLATE_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result remove(Long id) {
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(id, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		TemplateService templateService = TemplateServiceFactory.get();
		templateService.delete(template);
		GenerateServiceFactory.build(template).dropTable();
		removeDomainClass(template);
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		TemplateService templateService = TemplateServiceFactory.get();
		for (Long id : ids) {
			Template template = TemplateUtils.getTemplate(id, userIds);
			if (template != null) {
				templateService.delete(template);
				GenerateServiceFactory.build(template).dropTable();
				removeDomainClass(template);
			}
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(messageService.getMessage("template.management")).createUrl("/template/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("template.management")).active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("navigationName").label(messageService.getMessage("navigation")).views(Views.TEXT.getView()).width(100).build());
		columns.add(Column.builder().field("title").label(messageService.getMessage("title")).views(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("functions").label(messageService.getMessage("functions")).views(Views.TEXT.getView()).width(300).build());
		columns.add(Column.builder().field("createTime").label(messageService.getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").views(Views.TEXT.getView()).width(150).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(messageService.getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").views(Views.TEXT.getView()).width(150).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("operator").label(messageService.getMessage("operator")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("action").label(messageService.getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("view").to("/management/main/").icon("list").text(messageService.getMessage("view")).variant("outline-secondary").type("link").build());
		actions.add(Action.builder().key("edit").to("/template/edit?id=").icon("edit").text(messageService.getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("remove").to("/template/main/remove").icon("trash").text(messageService.getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("path");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("navigation")));
		Map<String, FieldFilter> value = new HashMap<>(4);
		Map<String, String> navigationMap = resourceSelection.selectWithParent(null, Resource.ContentType.NAVIGATION);
		List<String> navigationValues = new ArrayList<>(navigationMap.size());
		List<String> navigationTexts = new ArrayList<>(navigationMap.size());
		for (Entry<String, String> entry : navigationMap.entrySet()) {
			navigationValues.add(entry.getKey());
			navigationTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), navigationValues, navigationTexts));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("title");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		value = new HashMap<>(4);
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("createTime", "updateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>(4);
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("operatorId");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operator")));
		value = new HashMap<>(4);
		Map<String, String> userMap = userSelection.select();
		List<String> userValues = new ArrayList<>(userMap.size());
		List<String> userTexts = new ArrayList<>(userMap.size());
		for (Entry<String, String> entry : userMap.entrySet()) {
			userValues.add(entry.getKey());
			userTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), userValues, userTexts));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}
	
	@Override
	protected List<Functions> functions(Object key) {
		return SUPPORT_FUNCTIONS;
	}
	
	private TemplateInfo buildTemplateInfo(Template template) {
		TemplateInfo templateInfo = new TemplateInfo();
		templateInfo.setId(template.getId().toString());
		templateInfo.setTitle(template.getTitle());
		if (template.getFunctions() != null && template.getFunctions() != 0) {
			List<Functions> funcs = TemplateUtils.getFunctions(template);
			String functions = funcs.stream().map(func -> {
				return messageService.getMessage(SystemUtils.humpToCode(func.getFunction(), "."));
			}).collect(Collectors.joining(", "));
			templateInfo.setFunctions(functions);
		}
		templateInfo.setCreateTime(template.getCreateTime());
		templateInfo.setUpdateTime(template.getUpdateTime());
		return templateInfo;
	}
	
	private void removeDomainClass(Template template) {
		if (template == null) {
			return;
		}
		String sign = template.sign();
		if (sign == null) {
			return;
		}
		String generateClassName = domainGenerater.buildGenerateName(sign);
		domainGenerater.removeDomainClass(TemplateUtils.getNamespace(template), generateClassName);
	}
	
}
