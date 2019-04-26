package net.saisimon.agtms.web.controller.user;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.BaseController;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;
import net.saisimon.agtms.web.dto.req.UserProfileSaveParam;
import net.saisimon.agtms.web.dto.resp.ProfileInfo;

/**
 * 用户信息控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping(path = "/user")
@ControllerInfo("user")
public class UserController extends BaseController {
	
	/**
	 * 校验用户信息
	 * 
	 * @param param 用户信息
	 * @param result
	 * @return 用户信息正确返回成功响应，否则返回失败原因响应
	 */
	@Operate(type=OperateTypes.LOGIN)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/auth")
	public Result auth(@Validated @RequestBody UserAuthParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		UserService userService = UserServiceFactory.get();
		User user = userService.auth(param.getName(), param.getPassword());
		if (user == null) {
			return ErrorMessage.User.USERNAME_OR_PASSWORD_NOT_CORRECT;
		}
		if (UserStatuses.LOCKED.getStatus().equals(user.getStatus())) {
			return ErrorMessage.User.ACCOUNT_LOCKED;
		}
		user.setLastLoginTime(new Date());
		userService.saveOrUpdate(user);
		UserToken token = buildToken(user);
		TokenFactory.get().setToken(user.getId(), token);
		return ResultUtils.simpleSuccess(token);
	}
	
	/**
	 * 用户修改密码
	 * 
	 * @param param 密码信息
	 * @param result 
	 * @return 密码信息正确返回成功响应，否则返回失败原因响应
	 */
	@Operate(type=OperateTypes.EDIT, value="change.password")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/password/change")
	public Result passwordChange(@Validated @RequestBody UserPasswordChangeParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (param.getOldPassword().length() > 16) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("old.password"), 16);
		}
		if (param.getNewPassword().length() > 16) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("new.password"), 16);
		}
		Long userId = AuthUtils.getUid();
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(userId);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		String oldHmacPassword = AuthUtils.hmac(param.getOldPassword(), user.getSalt());
		if (!oldHmacPassword.equals(user.getPassword())) {
			return ErrorMessage.User.OLD_PASSWORD_NOT_CORRECT;
		}
		String hmacPassword = AuthUtils.hmac(param.getNewPassword(), user.getSalt());
		user.setPassword(hmacPassword);
		if (UserStatuses.CREATED.getStatus().equals(user.getStatus())) {
			user.setStatus(UserStatuses.NORMAL.getStatus());
		}
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
		return ResultUtils.simpleSuccess();
	}
	
	/**
	 * 用户个人资料
	 * 
	 */
	@Operate(type=OperateTypes.QUERY, value="profile")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/profile/info")
	public Result profileInfo() {
		Long userId = AuthUtils.getUid();
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(userId);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setLoginName(user.getLoginName());
		profileInfo.setAvatar(user.getAvatar());
		profileInfo.setNickname(user.getNickname());
		profileInfo.setRemark(user.getRemark());
		profileInfo.setCellphone(user.getCellphone());
		profileInfo.setEmail(user.getEmail());
		return ResultUtils.simpleSuccess(profileInfo);
	}
	
	/**
	 * 用户编辑个人资料
	 * 
	 * @param param 个人资料信息
	 * @param result
	 * @return 个人资料信息正确返回成功响应，否则返回失败原因响应
	 */
	@Operate(type=OperateTypes.EDIT, value="edit.profile")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/profile/save")
	public Result profileSave(@Validated @RequestBody UserProfileSaveParam param) {
		if (param.getNickname() != null && param.getNickname().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("nickname"), 32);
		}
		if (param.getAvatar() != null && param.getAvatar().length() > 64) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("avatar"), 64);
		}
		if (param.getRemark() != null && param.getRemark().length() > 512) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("remark"), 512);
		}
		Long userId = AuthUtils.getUid();
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(userId);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		user.setAvatar(param.getAvatar());
		user.setNickname(param.getNickname());
		user.setRemark(param.getRemark());
		user.setUpdateTime(new Date());
		userService.saveOrUpdate(user);
		return ResultUtils.simpleSuccess();
	}
	
	/**
	 * 用户登出
	 * 
	 * @return 登出结果响应
	 */
	@Operate(type=OperateTypes.LOGOUT)
	@RequestMapping("/logout")
	public Result logout() {
		Long uid = AuthUtils.getUid();
		TokenFactory.get().setToken(uid, null);
		return ResultUtils.simpleSuccess(uid);
	}
	
	private UserToken buildToken(User user) {
		UserToken token = new UserToken();
		token.setExpireTime(AuthUtils.getExpireTime());
		token.setToken(AuthUtils.createToken());
		token.setUserId(user.getId());
		token.setStatus(user.getStatus());
		token.setAdmin(user.isAdmin());
		token.setLoginName(user.getLoginName());
		token.setAvatar(user.getAvatar());
		return token;
	}
	
}
