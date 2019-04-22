package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.Admin;
import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.enums.Whether;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.AbstractEditController;
import net.saisimon.agtms.web.controller.main.UserMainController;
import net.saisimon.agtms.web.dto.req.UserParam;
import net.saisimon.agtms.web.selection.WhetherSelection;

/**
 * 用户编辑控制器
 * 
 * @author saisimon
 *
 */
@Admin
@RestController
@RequestMapping("/user/edit")
@ControllerInfo("user.management")
public class UserEditController extends AbstractEditController<User> {
	
	@Autowired
	private WhetherSelection whetherSelection;
	
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		User user = null;
		if (id != null) {
			UserService userService = UserServiceFactory.get();
			Optional<User> optional = userService.findById(id);
			if (!optional.isPresent()) {
				return ErrorMessage.User.ACCOUNT_NOT_EXIST;
			}
			user = optional.get();
		}
		return ResultUtils.simpleSuccess(getEditGrid(user, UserMainController.USER));
	}
	
	@Operate(type=OperateTypes.EDIT)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/save")
	public Result save(@Validated @RequestBody UserParam body, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (body.getLoginName().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("login.name"), 32);
		}
		if (body.getPassword().length() > 16) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("password"), 16);
		}
		if (body.getNickname().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("nickname"), 32);
		}
		if (body.getCellphone().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("cellphone"), 32);
		}
		if (body.getEmail().length() > 256) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("email"), 256);
		}
		if (body.getAvatar().length() > 64) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("avatar"), 64);
		}
		if (body.getRemark().length() > 512) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("remark"), 512);
		}
		UserService userService = UserServiceFactory.get();
		Long id = body.getId();
		if (null != id && id > 0) {
			Optional<User> optional = userService.findById(id);
			if (!optional.isPresent()) {
				return ErrorMessage.User.ACCOUNT_NOT_EXIST;
			}
			if (userService.exists(id, body.getLoginName(), body.getCellphone(), body.getEmail())) {
				return ErrorMessage.User.ACCOUNT_ALREADY_EXISTS;
			}
			User oldUser = optional.get();
			oldUser.setLoginName(body.getLoginName());
			oldUser.setCellphone(body.getCellphone());
			oldUser.setEmail(body.getEmail());
			oldUser.setUpdateTime(new Date());
			oldUser.setAdmin(Whether.YES.getValue().equals(body.getAdmin()));
			if (SystemUtils.isNotBlank(body.getNickname())) {
				oldUser.setNickname(body.getNickname());
			}
			if (SystemUtils.isNotBlank(body.getAvatar())) {
				oldUser.setAvatar(body.getAvatar());
			}
			if (SystemUtils.isNotBlank(body.getRemark())) {
				oldUser.setRemark(body.getRemark());
			}
			userService.saveOrUpdate(oldUser);
		} else {
			if (SystemUtils.isBlank(body.getPassword())) {
				return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
			}
			if (userService.exists(null, body.getLoginName(), body.getCellphone(), body.getEmail())) {
				return ErrorMessage.User.ACCOUNT_ALREADY_EXISTS;
			}
			User user = new User();
			user.setLoginName(body.getLoginName());
			user.setCellphone(body.getCellphone());
			user.setEmail(body.getEmail());
			String salt = AuthUtils.createSalt();
			String hmacPwd = AuthUtils.hmac(body.getPassword(), salt);
			Date time = new Date();
			user.setCreateTime(time);
			user.setUpdateTime(time);
			user.setSalt(salt);
			user.setPassword(hmacPwd);
			user.setNickname(body.getNickname());
			user.setAvatar(body.getAvatar());
			user.setAdmin(Whether.YES.getValue().equals(body.getAdmin()));
			user.setStatus(UserStatuses.CREATED.getStatus());
			user.setRemark(body.getRemark());
			userService.saveOrUpdate(user);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(User user, Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("user.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("user.management")).to("/user/main").build());
		if (user == null) {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	@Override
	protected List<Field<?>> fields(User user, Object key) {
		List<Field<?>> fields = new ArrayList<>();
		Field<String> loginNameField = Field.<String>builder().name("loginName").text(getMessage("login.name")).type(Classes.STRING.getName()).required(true).build();
		Field<String> passwordField = Field.<String>builder().name("password").text(getMessage("password")).type(Classes.STRING.getName()).views(Views.PASSWORD.getView()).required(true).build();
		Field<String> nicknameField = Field.<String>builder().name("nickname").text(getMessage("nickname")).type(Classes.STRING.getName()).build();
		Field<String> cellphoneField = Field.<String>builder().name("cellphone").text(getMessage("cellphone")).type(Classes.STRING.getName()).views(Views.PHONE.getView()).required(true).build();
		Field<String> emailField = Field.<String>builder().name("email").text(getMessage("email")).type(Classes.STRING.getName()).views(Views.EMAIL.getView()).required(true).build();
		Field<String> avatarField = Field.<String>builder().name("avatar").text(getMessage("avatar")).type(Classes.STRING.getName()).views(Views.IMAGE.getView()).build();
		List<Option<Integer>> whetherOptions = Select.buildOptions(whetherSelection.select());
		Field<Option<Integer>> adminField = Field.<Option<Integer>>builder().name("admin").text(getMessage("admin")).views(Views.SELECTION.getView()).options(whetherOptions).build();
		Field<String> remarkField = Field.<String>builder().name("remark").text(getMessage("remark")).type(Classes.STRING.getName()).views(Views.TEXTAREA.getView()).build();
		if (user != null) {
			loginNameField.setValue(user.getLoginName());
			passwordField.setDisabled(true);
			passwordField.setValue("******");
			nicknameField.setValue(user.getNickname());
			cellphoneField.setValue(user.getCellphone());
			emailField.setValue(user.getEmail());
			avatarField.setValue(user.getAvatar());
			adminField.setValue(Select.getOption(whetherOptions, user.isAdmin() ? 1 : 0));
			remarkField.setValue(user.getRemark());
		} else {
			loginNameField.setValue("");
			passwordField.setValue("");
			nicknameField.setValue("");
			cellphoneField.setValue("");
			emailField.setValue("");
			avatarField.setValue("");
			adminField.setValue(whetherOptions.get(0));
			remarkField.setValue("");
		}
		fields.add(loginNameField);
		fields.add(passwordField);
		fields.add(nicknameField);
		fields.add(cellphoneField);
		fields.add(emailField);
		fields.add(avatarField);
		fields.add(adminField);
		fields.add(remarkField);
		return fields;
	}
	
}
