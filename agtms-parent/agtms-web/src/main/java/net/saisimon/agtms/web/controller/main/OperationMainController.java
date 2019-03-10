package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.OperationServiceFactory;
import net.saisimon.agtms.core.service.OperationService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.OperationInfo;
import net.saisimon.agtms.web.selection.OperateTypeSelection;

/**
 * 操作记录主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/operation/main")
public class OperationMainController extends MainController {
	
	public static final String OPERATION = "operation";
	private static final String OPERATION_FILTERS = OPERATION + "_filters";
	private static final String OPERATION_PAGEABLE = OPERATION + "_pageable";
	private static final Set<String> OPERATION_FILTER_FIELDS = new HashSet<>();
	static {
		OPERATION_FILTER_FIELDS.add("operateType");
		OPERATION_FILTER_FIELDS.add("operateTime");
		OPERATION_FILTER_FIELDS.add("operateIp");
	}
	
	@Autowired
	private OperateTypeSelection operateTypeSelection;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(OPERATION));
	}
	
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		FilterRequest filter = FilterRequest.build(body, OPERATION_FILTER_FIELDS);
		filter.and(Constant.OPERATORID, AuthUtils.getUserInfo().getUserId());
		FilterPageable pageable = FilterPageable.build(param);
		OperationService operationService = OperationServiceFactory.get();
		Page<Operation> page = operationService.findPage(filter, pageable);
		List<OperationInfo> results = new ArrayList<>(page.getContent().size());
		Map<Integer, String> operateTypeMap = operateTypeSelection.select();
		for (Operation operation : page.getContent()) {
			OperationInfo result = new OperationInfo();
			result.setId(operation.getId());
			result.setOperateTime(operation.getOperateTime());
			result.setOperateType(operateTypeMap.get(operation.getOperateType()));
			result.setOperateIp(operation.getOperateIp());
			if (operation.getOperateContent() != null) {
				result.setOperateContent(Arrays.stream(operation.getOperateContent().split(",")).map(msg -> { return getMessage(msg);}).collect(Collectors.joining(" / ")));
			}
			result.setAction(OPERATION);
			results.add(result);
		}
		request.getSession().setAttribute(OPERATION_FILTERS, body);
		request.getSession().setAttribute(OPERATION_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("operation.management")).build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("operation.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("operateType", "operateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operate.type", "operate.time")));
		Map<String, FieldFilter> value = new HashMap<>();
		Map<Integer, String> operateTypeMap = operateTypeSelection.select();
		List<Integer> values = new ArrayList<>(operateTypeMap.keySet());
		List<String> texts = new ArrayList<>(operateTypeMap.values());
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), values, texts));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("operateContent").label(getMessage("operate.content")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("operateType").label(getMessage("operate.type")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("operateTime").label(getMessage("operate.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("operateIp").label(getMessage("operate.ip")).view(Views.TEXT.getView()).width(200).build());
		return columns;
	}

}
