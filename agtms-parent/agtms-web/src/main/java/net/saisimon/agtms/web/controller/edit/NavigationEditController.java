package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.EditController;
import net.saisimon.agtms.web.controller.main.NavigationMainController;
import net.saisimon.agtms.web.dto.req.NavigationParam;
import net.saisimon.agtms.web.selection.NavigationSelection;

/**
 * 导航编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/navigation/edit")
@ControllerInfo("navigation.management")
public class NavigationEditController extends EditController {
	
	@Autowired
	private NavigationSelection navigationSelection;
	
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		return ResultUtils.simpleSuccess(getEditGrid(id, NavigationMainController.NAVIGATION));
	}
	
	@Operate(type=OperateTypes.EDIT)
	@Transactional
	@PostMapping("/save")
	public Result save(@Validated @RequestBody NavigationParam body, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		NavigationService navigationService = NavigationServiceFactory.get();
		long userId = AuthUtils.getUserInfo().getUserId();
		Long id = body.getId();
		if (null != id && id > 0) {
			Navigation oldNavigation = navigationService.getNavigation(id, userId);
			if (oldNavigation == null) {
				return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
			}
			if (!body.getTitle().equals(oldNavigation.getTitle())) {
				if (navigationService.exists(body.getTitle(), userId)) {
					return ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS;
				}
			}
			Map<Long, String> navigationMap = navigationSelection.selectWithParent(oldNavigation.getId());
			if (!navigationMap.containsKey(oldNavigation.getParentId())) {
				return ErrorMessage.Common.PARAM_ERROR;
			}
			oldNavigation.setParentId(body.getParentId());
			oldNavigation.setIcon(body.getIcon());
			oldNavigation.setTitle(body.getTitle());
			oldNavigation.setPriority(body.getPriority());
			oldNavigation.setUpdateTime(new Date());
			navigationService.saveOrUpdate(oldNavigation);
		} else {
			if (navigationService.exists(body.getTitle(), userId)) {
				return ErrorMessage.Navigation.NAVIGATION_ALREADY_EXISTS;
			}
			Map<Long, String> navigationMap = navigationSelection.selectWithParent(null);
			if (!navigationMap.containsKey(body.getParentId())) {
				return ErrorMessage.Common.PARAM_ERROR;
			}
			Date time = new Date();
			Navigation newNavigation = new Navigation();
			newNavigation.setParentId(body.getParentId());
			newNavigation.setIcon(body.getIcon());
			newNavigation.setTitle(body.getTitle());
			newNavigation.setPriority(body.getPriority());
			newNavigation.setCreateTime(time);
			newNavigation.setOperatorId(userId);
			newNavigation.setUpdateTime(time);
			navigationService.saveOrUpdate(newNavigation);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(Long id, Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("navigation.management")).to("/navigation/main").build());
		if (id == null) {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	@Override
	protected List<Field<?>> fields(Long id, Object key) {
		Long nid = (Long) id;
		Navigation navigation = null;
		if (nid != null) {
			long userId = AuthUtils.getUserInfo().getUserId();
			NavigationService navigationService = NavigationServiceFactory.get();
			navigation = navigationService.getNavigation(nid, userId);
		}
		List<Field<?>> fields = new ArrayList<>();
		List<Option<Long>> options = Select.buildOptions(navigationSelection.selectWithParent(nid));
		Field<Option<Long>> parentIdField = Field.<Option<Long>>builder().name("parentId").text(getMessage("parent.navigation")).required(true).type("select").options(options).build();
		Field<String> iconField = Field.<String>builder().name("icon").text(getMessage("icon")).type(Classes.STRING.getName()).required(true).view("icon").build();
		Field<String> titleField = Field.<String>builder().name("title").text(getMessage("title")).type(Classes.STRING.getName()).required(true).build();
		Field<Long> priorityField = Field.<Long>builder().name("priority").text(getMessage("priority")).type(Classes.LONG.getName()).build();
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
			iconField.setValue(Navigation.DEFAULT_ICON);
			titleField.setValue("");
			priorityField.setValue(Navigation.DEFAULT_PRIORITY);
		}
		fields.add(parentIdField);
		fields.add(iconField);
		fields.add(titleField);
		fields.add(priorityField);
		return fields;
	}
	
}
