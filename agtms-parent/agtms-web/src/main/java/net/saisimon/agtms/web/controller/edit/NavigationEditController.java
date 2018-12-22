package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.date.DateUtil;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.EditController;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.dto.resp.NavigationInfo;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.selection.NavigationSelection;
import net.saisimon.agtms.web.util.ResultUtils;

@RestController
@RequestMapping("/navigate/edit")
public class NavigationEditController extends EditController {
	
	@Autowired
	private NavigationSelection navigationSelection;
	
	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("navigate.management")).to("/navigate/main").build());
		return breadcrumbs;
	}
	
	@Override
	protected List<Field<?>> fields(Long id, Object key) {
		Long nid = (Long) id;
		Navigation navigation = null;
		if (nid != null) {
			long userId = TokenUtils.getUserInfo().getUserId();
			NavigationService navigationService = NavigationServiceFactory.get();
			navigation = navigationService.getNavigation(nid, userId);
		}
		List<Field<?>> fields = new ArrayList<>();
		List<Option<Long>> options = Select.buildOptions(navigationSelection.selectWithParent(nid));
		Field<Option<Long>> parentIdField = Field.<Option<Long>>builder().name("parentId").text(getMessage("parent.navigate")).type("select").options(options).build();
		Field<String> iconField = Field.<String>builder().name("icon").text(getMessage("icon")).type("text").view("icon").build();
		Field<String> titleField = Field.<String>builder().name("title").text(getMessage("title")).type("text").required(true).build();
		Field<Integer> priorityField = Field.<Integer>builder().name("priority").text(getMessage("priority")).type("number").required(true).build();
		if (navigation != null) {
			Option<Long> select = null;
			for (Option<Long> option : options) {
				if (option.getValue().equals(navigation.getParentId())) {
					select = option;
					break;
				}
			}
			parentIdField.setValue(select);
			iconField.setValue(navigation.getIcon());
			titleField.setValue(navigation.getTitle());
			priorityField.setValue(navigation.getPriority());
		} else {
			parentIdField.setValue(options.get(0));
			iconField.setValue("cog");
			titleField.setValue("");
			priorityField.setValue(0);
		}
		fields.add(parentIdField);
		fields.add(iconField);
		fields.add(titleField);
		fields.add(priorityField);
		return fields;
	}

	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		return ResultUtils.success(getEditGrid(id, "navigate"));
	}
	
	@PostMapping("/info")
	public Result info(@RequestParam("id") Long id) {
		long userId = TokenUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		return ResultUtils.success(buildNavigationResult(navigationService.getNavigation(id, userId)));
	}
	
	@PostMapping("/save")
	public Result save(@Validated @RequestBody NavigationParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		NavigationService navigationService = NavigationServiceFactory.get();
		long userId = TokenUtils.getUserInfo().getUserId();
		String time = DateUtil.formatDateTime(new Date());
		Long id = param.getId();
		if (null != id && id > 0) {
			Navigation oldNavigation = navigationService.getNavigation(id, userId);
			if (oldNavigation == null) {
				return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
			}
			if (!param.getTitle().equals(oldNavigation.getTitle())) {
				if (navigationService.existNavigation(param.getTitle(), userId)) {
					return ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS;
				}
			}
			Map<Long, String> navigationMap = navigationSelection.selectWithParent(oldNavigation.getId());
			if (!navigationMap.containsKey(oldNavigation.getParentId())) {
				return ErrorMessage.Common.PARAM_ERROR;
			}
			oldNavigation.setParentId(param.getParentId());
			oldNavigation.setIcon(param.getIcon());
			oldNavigation.setTitle(param.getTitle());
			oldNavigation.setPriority(param.getPriority());
			oldNavigation.setUpdateTime(time);
			navigationService.saveOrUpdate(oldNavigation);
		} else {
			if (navigationService.existNavigation(param.getTitle(), userId)) {
				return ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS;
			}
			Map<Long, String> navigationMap = navigationSelection.selectWithParent(null);
			if (!navigationMap.containsKey(param.getParentId())) {
				return ErrorMessage.Common.PARAM_ERROR;
			}
			Navigation newNavigation = new Navigation();
			newNavigation.setParentId(param.getParentId());
			newNavigation.setIcon(param.getIcon());
			newNavigation.setTitle(param.getTitle());
			newNavigation.setPriority(param.getPriority());
			newNavigation.setCreateTime(time);
			newNavigation.setBelong(userId);
			newNavigation.setUpdateTime(time);
			navigationService.saveOrUpdate(newNavigation);
		}
		return ResultUtils.success();
	}
	
	private NavigationInfo buildNavigationResult(Navigation navigation) {
		if (navigation == null) {
			return null;
		}
		NavigationInfo result = new NavigationInfo();
		BeanUtils.copyProperties(navigation, result);
		return result;
	}

}
