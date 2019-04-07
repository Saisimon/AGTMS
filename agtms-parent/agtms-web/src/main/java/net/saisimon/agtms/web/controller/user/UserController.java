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
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserChangePasswordParam;

/**
 * 用户信息控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping(path = "/user")
@ControllerInfo("user")
public class UserController {
	
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
		UserToken token = buildToken(user.getId(), user.isAdmin());
		TokenFactory.get().setToken(user.getId(), token);
		return ResultUtils.simpleSuccess(token);
	}
	
	@Operate(type=OperateTypes.EDIT, value="change.password")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/change/password")
	public Result changePassword(@Validated @RequestBody UserChangePasswordParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = AuthUtils.getUid();
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(userId);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		String hmacPassword = AuthUtils.hmac(param.getPassword(), user.getSalt());
		user.setPassword(hmacPassword);
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
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
	
	private UserToken buildToken(Long userId, boolean admin) {
		UserToken token = new UserToken();
		token.setExpireTime(AuthUtils.getExpireTime());
		token.setToken(AuthUtils.createToken());
		token.setUserId(userId);
		token.setAdmin(admin);
		return token;
	}
	
}
