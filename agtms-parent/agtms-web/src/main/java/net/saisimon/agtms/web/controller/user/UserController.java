package net.saisimon.agtms.web.controller.user;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserRegisterParam;

@RestController
@RequestMapping(path = "/user")
public class UserController {
	
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
	
	@RequestMapping("/logout")
	public Result logout() {
		AuthUtils.setUserInfo(AuthUtils.getUid(), null);
		return ResultUtils.simpleSuccess();
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
