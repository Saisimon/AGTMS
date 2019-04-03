package net.saisimon.agtms.web.controller.user;

import java.util.Date;

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
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserRegisterParam;

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
		user.setLastLoginTime(new Date());
		userService.saveOrUpdate(user);
		UserToken token = buildToken(user.getId());
		TokenFactory.get().setToken(user.getId(), token);
		return ResultUtils.simpleSuccess(token);
	}
	
	/**
	 * 注册用户信息
	 * 
	 * @param param 用户注册信息
	 * @param result
	 * @return 注册结果响应
	 */
	@Operate(type=OperateTypes.REGISTER)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/register")
	public Result register(@Validated @RequestBody UserRegisterParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (!SystemUtils.isEmail(param.getEmail())) {
			return ErrorMessage.User.EMAIL_FORMAT_ERROR;
		}
		UserService userService = UserServiceFactory.get();
		User user = userService.register(param.getName(), param.getEmail(), param.getPassword());
		if (user == null) {
			return ErrorMessage.User.ACCOUNT_ALREADY_EXISTS;
		}
		UserToken token = buildToken(user.getId());
		TokenFactory.get().setToken(user.getId(), token);
		return ResultUtils.simpleSuccess(token);
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
	
	private UserToken buildToken(Long userId) {
		UserToken token = new UserToken();
		token.setExpireTime(AuthUtils.getExpireTime());
		token.setToken(AuthUtils.createToken());
		token.setUserId(userId);
		return token;
	}
	
}
