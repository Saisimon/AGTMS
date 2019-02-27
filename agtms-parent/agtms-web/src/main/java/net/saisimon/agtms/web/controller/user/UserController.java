package net.saisimon.agtms.web.controller.user;

import javax.transaction.Transactional;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
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
	@Transactional
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
		UserInfo userInfo = buildUserInfo(user);
		userInfo.setToken(AuthUtils.createToken());
		AuthUtils.setUserInfo(userInfo.getUserId().toString(), userInfo);
		return ResultUtils.simpleSuccess(userInfo);
	}
	
	/**
	 * 注册用户信息
	 * 
	 * @param param 用户注册信息
	 * @param result
	 * @return 注册结果响应
	 */
	@Operate(type=OperateTypes.REGISTER)
	@Transactional
	@PostMapping("/register")
	public Result register(@Validated @RequestBody UserRegisterParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		UserService userService = UserServiceFactory.get();
		User user = userService.register(param.getName(), param.getEmail(), param.getPassword());
		if (user == null) {
			return ErrorMessage.User.ACCOUNT_ALREADY_EXISTS;
		}
		UserInfo userInfo = buildUserInfo(user);
		userInfo.setToken(AuthUtils.createToken());
		AuthUtils.setUserInfo(userInfo.getUserId().toString(), userInfo);
		return ResultUtils.simpleSuccess(userInfo);
	}
	
	/**
	 * 用户登出
	 * 
	 * @return 登出结果响应
	 */
	@Operate(type=OperateTypes.LOGOUT)
	@RequestMapping("/logout")
	public Result logout() {
		String uid = AuthUtils.getUid();
		AuthUtils.setUserInfo(uid, null);
		return ResultUtils.simpleSuccess(uid);
	}
	
	public UserInfo buildUserInfo(User user) {
		if (user == null) {
			return null;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getId());
		userInfo.setLoginName(user.getLoginName());
		return userInfo;
	}
	
}
