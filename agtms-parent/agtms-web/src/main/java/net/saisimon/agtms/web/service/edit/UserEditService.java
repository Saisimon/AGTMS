package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid.FieldGroup;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserParam;
import net.saisimon.agtms.web.selection.RoleSelection;
import net.saisimon.agtms.web.service.base.AbstractEditService;
import net.saisimon.agtms.web.service.main.UserMainService;

/**
 * 用户编辑服务
 * 
 * @author saisimon
 *
 */
@Service
public class UserEditService extends AbstractEditService<User> {
	
	@Autowired
	private RoleSelection roleSelection;
	@Autowired
	private UserMainService userMainService;
	
	public Result grid(Long id) {
		User user = null;
		if (id != null) {
			UserService userService = UserServiceFactory.get();
			Optional<User> optional = userService.findById(id);
			if (!optional.isPresent()) {
				return ErrorMessage.User.ACCOUNT_NOT_EXIST;
			}
			user = optional.get();
		}
		return ResultUtils.simpleSuccess(getEditGrid(user, UserMainService.USER));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result save(UserParam body) {
		Result checkResult = checkOverflow(body);
		if (!ResultUtils.isSuccess(checkResult)) {
			return checkResult;
		}
		if (null != body.getId() && body.getId() > 0) {
			return updateUser(body);
		} else {
			return saveUser(body);
		}
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(User user, Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("user.module"))
				.to("/").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("user.management"))
				.to("/user/main").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage(user == null ? "create" : "edit"))
				.active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<FieldGroup> groups(User user, Object key) {
		Field<String> loginNameField = Field.<String>builder()
				.name("loginName")
				.text(messageService.getMessage("login.name"))
				.type(Classes.STRING.getKey())
				.required(true).build();
		Field<String> passwordField = Field.<String>builder()
				.name("password")
				.text(messageService.getMessage("password"))
				.type(Classes.STRING.getKey())
				.views(Views.PASSWORD.getKey())
				.required(true).build();
		Field<Option<String>> rolesField = Field.<Option<String>>builder()
				.name("roles")
				.text(messageService.getMessage("role.name"))
				.type("select")
				.views(Views.SELECTION.getKey())
				.options(roleSelection.buildNestedOptions(null))
				.flat(true)
				.multiple(true).build();
		Field<String> nicknameField = Field.<String>builder()
				.name("nickname")
				.text(messageService.getMessage("nickname"))
				.type(Classes.STRING.getKey()).build();
		Field<String> cellphoneField = Field.<String>builder()
				.name("cellphone")
				.text(messageService.getMessage("cellphone"))
				.type(Classes.STRING.getKey())
				.views(Views.PHONE.getKey())
				.required(true).build();
		Field<String> emailField = Field.<String>builder()
				.name("email")
				.text(messageService.getMessage("email"))
				.type(Classes.STRING.getKey())
				.views(Views.EMAIL.getKey())
				.required(true).build();
		Field<String> avatarField = Field.<String>builder()
				.name("avatar")
				.text(messageService.getMessage("avatar"))
				.type(Classes.STRING.getKey())
				.views(Views.IMAGE.getKey()).build();
		Field<String> remarkField = Field.<String>builder()
				.name("remark")
				.text(messageService.getMessage("remark"))
				.type(Classes.STRING.getKey())
				.views(Views.TEXTAREA.getKey()).build();
		if (user != null) {
			loginNameField.setValue(user.getLoginName());
			passwordField.setDisabled(true);
			passwordField.setValue("******");
			rolesField.setValue(getUserRolePaths(user));
			nicknameField.setValue(user.getNickname());
			cellphoneField.setValue(user.getCellphone());
			emailField.setValue(user.getEmail());
			avatarField.setValue(user.getAvatar());
			remarkField.setValue(user.getRemark());
		} else {
			loginNameField.setValue("");
			passwordField.setValue("");
			rolesField.setValue(Collections.emptyList());
			nicknameField.setValue("");
			cellphoneField.setValue("");
			emailField.setValue("");
			avatarField.setValue("");
			remarkField.setValue("");
		}
		List<FieldGroup> groups = new ArrayList<>();
		groups.add(buildFieldGroup(messageService.getMessage("account.info"), 0, loginNameField, passwordField, rolesField));
		groups.add(buildFieldGroup(messageService.getMessage("personal.info"), 0, nicknameField, cellphoneField, emailField));
		groups.add(buildFieldGroup(messageService.getMessage("extra.info"), 0, avatarField, remarkField));
		return groups;
	}
	
	private List<String> getUserRolePaths(User user) {
		List<UserRole> userRoles = UserRoleServiceFactory.get().getUserRoles(user.getId());
		if (CollectionUtils.isEmpty(userRoles)) {
			return Collections.emptyList();
		}
		return userRoles.stream().map(userRole -> {
			return userRole.getRolePath() + "/" + userRole.getRoleId();
		}).collect(Collectors.toList());
	}
	
	private Result checkOverflow(UserParam body) {
		if (body.getLoginName().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("login.name"), 32);
		}
		if (body.getCellphone().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("cellphone"), 32);
		}
		if (body.getEmail().length() > 256) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("email"), 256);
		}
		if (body.getPassword() != null && body.getPassword().length() > 16) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("password"), 16);
		}
		if (body.getNickname() != null && body.getNickname().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("nickname"), 32);
		}
		if (body.getAvatar() != null && body.getAvatar().length() > 64) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("avatar"), 64);
		}
		if (body.getRemark() != null && body.getRemark().length() > 512) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("remark"), 512);
		}
		return ResultUtils.simpleSuccess();
	}
	
	private Result updateUser(UserParam body) {
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(body.getId());
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		if (userService.exists(body.getId(), body.getLoginName(), body.getCellphone(), body.getEmail())) {
			return ErrorMessage.User.ACCOUNT_ALREADY_EXISTS;
		}
		User oldUser = optional.get();
		oldUser.setLoginName(body.getLoginName());
		oldUser.setCellphone(body.getCellphone());
		oldUser.setEmail(body.getEmail());
		oldUser.setNickname(body.getNickname());
		oldUser.setAvatar(body.getAvatar());
		oldUser.setRemark(body.getRemark());
		oldUser.setUpdateTime(new Date());
		userService.saveOrUpdate(oldUser);
		userMainService.grantRole(oldUser.getId(), roleSelection.select(), body.getRoles());
		return ResultUtils.simpleSuccess();
	}
	
	private Result saveUser(UserParam body) {
		if (SystemUtils.isBlank(body.getPassword())) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		UserService userService = UserServiceFactory.get();
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
		user.setStatus(UserStatuses.CREATED.getStatus());
		user.setRemark(body.getRemark());
		userService.saveOrUpdate(user);
		userMainService.grantRole(user.getId(), roleSelection.select(), body.getRoles());
		return ResultUtils.simpleSuccess();
	}
	
}
