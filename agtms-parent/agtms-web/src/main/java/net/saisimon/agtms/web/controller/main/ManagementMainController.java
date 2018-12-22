package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.Filter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.grid.MainGrid.Title;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.enums.FilterTypes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.util.ResultUtils;

@RestController
@RequestMapping("/manage/main/{mid}")
public class ManagementMainController extends MainController {
	
	@PostMapping("/grid")
	public Result grid(@PathVariable("mid") Long mid) {
		return ResultUtils.success(getMainGrid(mid));
	}
	
	@PostMapping("/list")
	public Result list(@PathVariable("mid") Long mid, @RequestParam Map<String, Object> params, @RequestBody Map<String, Object> map) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, TokenUtils.getUserInfo().getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		FilterRequest filter = FilterRequest.build(map);
		FilterPageable pageable = FilterPageable.build(params);
		GenerateService generateService = GenerateServiceFactory.build(template);
		Page<Domain> page = generateService.findPage(filter, pageable);
		request.getSession().setAttribute(mid + "_filters", map);
		request.getSession().setAttribute(mid + "_pageable", params);
		return ResultUtils.pageSuccess(page.getContent(), page.getTotalElements());
	}
	
	@Override
	protected Header header(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, TokenUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		Header header = Header.builder().title(template.getTitle()).build();
		if (template.getFunctions().contains(Functions.CREATE.getFunction())) {
			header.setCreateUrl("/manage/edit/" + template.getId());
		}
		return header;
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		Long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, userId);
		if (template == null) {
			return null;
		}
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).active(true).build());
		Long nid = template.getNavigationId();
		if (nid != null && nid != -1) {
			NavigationService navigationService = NavigationServiceFactory.get();
			Map<Long, Navigation> navigateMap = navigationService.getNavigationMap(userId);
			do {
				Navigation navigation = navigateMap.get(nid);
				breadcrumbs.add(0, Breadcrumb.builder().text(navigation.getTitle()).to("/").build());
				nid = navigation.getParentId();
			} while (nid != null && nid != -1);
		}
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, TokenUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		List<Filter> filters = new ArrayList<>();
		for (TemplateColumn column : template.getColumns()) {
			Filter filter = new Filter();
			List<String> keyValues = new ArrayList<>();
			List<String> keyTexts = new ArrayList<>();
			Map<String, FieldFilter> value = new HashMap<>();
			for (TemplateField field : column.getFields()) {
				if (StringUtils.isBlank(field.getFilterType())) {
					continue;
				}
				String fieldName = column.getColumnName() + field.getFieldName();
				keyValues.add(fieldName);
				keyTexts.add(field.getFieldTitle());
				if (FilterTypes.TEXT.name().equalsIgnoreCase(field.getFilterType())) {
					value.put(fieldName, TextFilter.textFilter("", "text", "strict"));
				} else if (FilterTypes.NUMBER.name().equalsIgnoreCase(field.getFilterType())) {
					value.put(fieldName, RangeFilter.rangeFilter("", "number", "", "number"));
				} else if (FilterTypes.DATE.name().equalsIgnoreCase(field.getFilterType())) {
					value.put(fieldName, RangeFilter.rangeFilter("", "date", "", "date"));
				} else if (FilterTypes.SELECTION.name().equalsIgnoreCase(field.getFilterType())) {
					// TODO
					
				}
			}
			if (keyValues.isEmpty()) {
				continue;
			}
			filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyTexts));
			filter.setValue(value);
			filters.add(filter);
		}
		return filters;
	}
	
	@Override
	protected List<List<Title>> titles(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, TokenUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		List<List<Title>> titles = new ArrayList<>();
		List<Title> firstTitle = new ArrayList<>();
		List<Title> secondTitle = new ArrayList<>();
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			if (column.getFields().size() == 1) {
				String fieldName = column.getColumnName() + column.getFields().get(0).getFieldName();
				firstTitle.add(Title.builder().fields(Arrays.asList(fieldName)).title(column.getTitle()).rowspan(2).build());
			} else {
				List<String> fields = new ArrayList<>();
				for (TemplateField field : column.getFields()) {
					String fieldName = column.getColumnName() + field.getFieldName();
					fields.add(fieldName);
					Title st = Title.builder().fields(Arrays.asList(fieldName)).title(field.getFieldTitle()).build();
					if (field.getSorted()) {
						st.setOrderBy("");
					}
					secondTitle.add(st);
				}
				firstTitle.add(Title.builder().fields(fields).title(column.getTitle()).colspan(fields.size()).build());
			}
		}
		titles.add(firstTitle);
		titles.add(secondTitle);
		return titles;
	}

	@Override
	protected List<Column> columns(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, TokenUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		List<Column> columns = new ArrayList<>();
		for (TemplateColumn templateColumn : template.getColumns()) {
			for (TemplateField templateField : templateColumn.getFields()) {
				String fieldName = templateColumn.getColumnName() + templateField.getFieldName();
				Column column = Column.builder().field(fieldName).title(templateField.getFieldTitle()).width(templateField.getWidth()).view(templateField.getView()).build();
				if (templateField.getSorted()) {
					column.setOrderBy("");
				}
				columns.add(column);
			}
		}
		return columns;
	}

	@Override
	protected List<String> functions(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, TokenUtils.getUserInfo().getUserId());
		if (template == null || template.getFunctions() == null) {
			return null;
		}
		return Arrays.asList(template.getFunctions().split(","));
	}
	
}
