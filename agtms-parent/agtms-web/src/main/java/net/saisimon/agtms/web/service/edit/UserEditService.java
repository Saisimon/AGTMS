package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserParam;
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
			return saveUser(body);
		} else {
			return updateUser(body);
		}
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
	
	private Result saveUser(UserParam body) {
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
		oldUser.setUpdateTime(new Date());
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
		return ResultUtils.simpleSuccess();
	}
	
	private Result updateUser(UserParam body) {
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
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(User user, Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("user.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("user.management")).to("/user/main").build());
		if (user == null) {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
	@Override
	protected List<Field<?>> fields(User user, Object key) {
		List<Field<?>> fields = new ArrayList<>();
		Field<String> loginNameField = Field.<String>builder().name("loginName").text(messageService.getMessage("login.name")).type(Classes.STRING.getName()).required(true).build();
		Field<String> passwordField = Field.<String>builder().name("password").text(messageService.getMessage("password")).type(Classes.STRING.getName()).views(Views.PASSWORD.getView()).required(true).build();
		Field<String> nicknameField = Field.<String>builder().name("nickname").text(messageService.getMessage("nickname")).type(Classes.STRING.getName()).build();
		Field<String> cellphoneField = Field.<String>builder().name("cellphone").text(messageService.getMessage("cellphone")).type(Classes.STRING.getName()).views(Views.PHONE.getView()).required(true).build();
		Field<String> emailField = Field.<String>builder().name("email").text(messageService.getMessage("email")).type(Classes.STRING.getName()).views(Views.EMAIL.getView()).required(true).build();
		Field<String> avatarField = Field.<String>builder().name("avatar").text(messageService.getMessage("avatar")).type(Classes.STRING.getName()).views(Views.IMAGE.getView()).build();
		Field<String> remarkField = Field.<String>builder().name("remark").text(messageService.getMessage("remark")).type(Classes.STRING.getName()).views(Views.TEXTAREA.getView()).build();
		if (user != null) {
			loginNameField.setValue(user.getLoginName());
			passwordField.setDisabled(true);
			passwordField.setValue("******");
			nicknameField.setValue(user.getNickname());
			cellphoneField.setValue(user.getCellphone());
			emailField.setValue(user.getEmail());
			avatarField.setValue(user.getAvatar());
			remarkField.setValue(user.getRemark());
		} else {
			loginNameField.setValue("");
			passwordField.setValue("");
			nicknameField.setValue("");
			cellphoneField.setValue("");
			emailField.setValue("");
			avatarField.setValue("");
			remarkField.setValue("");
		}
		fields.add(loginNameField);
		fields.add(passwordField);
		fields.add(nicknameField);
		fields.add(cellphoneField);
		fields.add(emailField);
		fields.add(avatarField);
		fields.add(remarkField);
		return fields;
	}
	
}
