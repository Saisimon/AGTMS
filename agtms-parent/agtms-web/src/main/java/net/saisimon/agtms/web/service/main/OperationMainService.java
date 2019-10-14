package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Operation;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.OperationServiceFactory;
import net.saisimon.agtms.core.service.OperationService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.dto.resp.OperationInfo;
import net.saisimon.agtms.web.selection.OperateTypeSelection;
import net.saisimon.agtms.web.selection.UserSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 操作记录主服务
 * 
 * @author saisimon
 *
 */
@Service
public class OperationMainService extends AbstractMainService {
	
	public static final String OPERATION = "operation";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW
	);
	
	private static final String OPERATION_FILTERS = OPERATION + FILTER_SUFFIX;
	private static final String OPERATION_PAGEABLE = OPERATION + PAGEABLE_SUFFIX;
	private static final Set<String> OPERATION_FILTER_FIELDS = new HashSet<>();
	static {
		OPERATION_FILTER_FIELDS.add("operateType");
		OPERATION_FILTER_FIELDS.add("operateTime");
		OPERATION_FILTER_FIELDS.add("operateIp");
		OPERATION_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Autowired
	private OperateTypeSelection operateTypeSelection;
	@Autowired
	private UserSelection userSelection;
	@Autowired
	private PremissionService premissionService;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(OPERATION));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, OPERATION_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, premissionService.getUserIds(AuthUtils.getUid()), Constant.Operator.IN);
		FilterPageable pageable = FilterPageable.build(pageableMap);
		OperationService operationService = OperationServiceFactory.get();
		List<Operation> list = operationService.findPage(filter, pageable, false).getContent();
		List<OperationInfo> results = new ArrayList<>(list.size());
		Map<Integer, String> operateTypeMap = operateTypeSelection.select();
		Map<String, String> userMap = userSelection.select();
		for (Operation operation : list) {
			OperationInfo result = new OperationInfo();
			result.setId(operation.getId().toString());
			result.setOperateTime(operation.getOperateTime());
			result.setOperateType(operateTypeMap.get(operation.getOperateType()));
			result.setOperateIp(operation.getOperateIp());
			if (operation.getOperateContent() != null) {
				result.setOperateContent(Arrays.stream(operation.getOperateContent().split(",")).map(msg -> { return messageService.getMessage(msg);}).collect(Collectors.joining(" / ")));
			}
			result.setOperator(userMap.get(operation.getOperatorId().toString()));
			result.setAction(OPERATION);
			results.add(result);
		}
		request.getSession().setAttribute(OPERATION_FILTERS, filterMap);
		request.getSession().setAttribute(OPERATION_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Override
	protected Header header(Object key, List<Functions> functions) {
		return Header.builder().title(messageService.getMessage("operation.management")).build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("operation.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("operateType", "operateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operate.type", "operate.time")));
		Map<String, FieldFilter> value = new HashMap<>(4);
		Map<Integer, String> operateTypeMap = operateTypeSelection.select();
		List<Integer> values = new ArrayList<>(operateTypeMap.keySet());
		List<String> texts = new ArrayList<>(operateTypeMap.values());
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), values, texts));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList(Constant.OPERATORID);
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operator")));
		value = new HashMap<>(4);
		Map<String, String> userMap = userSelection.select();
		List<String> userValues = new ArrayList<>(userMap.size());
		List<String> userTexts = new ArrayList<>(userMap.size());
		for (Entry<String, String> entry : userMap.entrySet()) {
			userValues.add(entry.getKey());
			userTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), userValues, userTexts));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("operateContent").label(messageService.getMessage("operate.content")).views(Views.TEXT.getKey()).width(200).build());
		columns.add(Column.builder().field("operateType").label(messageService.getMessage("operate.type")).views(Views.TEXT.getKey()).width(200).build());
		columns.add(Column.builder().field("operateTime").label(messageService.getMessage("operate.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getKey()).orderBy("").build());
		columns.add(Column.builder().field("operateIp").label(messageService.getMessage("operate.ip")).views(Views.TEXT.getKey()).width(200).build());
		columns.add(Column.builder().field("operator").label(messageService.getMessage("operator")).width(200).views(Views.TEXT.getKey()).build());
		return columns;
	}

	@Override
	protected List<Functions> functions(Object key) {
		return functions("/operation/main", null, SUPPORT_FUNCTIONS);
	}
	
}
