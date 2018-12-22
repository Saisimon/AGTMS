package net.saisimon.agtms.web.controller.user;

import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserRegisterParam;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.util.ResultUtils;

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
		String token = TokenUtils.createToken();
		TokenUtils.setUserInfo(token, userInfo);
		return ResultUtils.success(token);
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
		String token = TokenUtils.createToken();
		TokenUtils.setUserInfo(token, userInfo);
		return ResultUtils.success(token);
	}
	
	@RequestMapping("/logout")
	public Result logout() {
		String token = TokenUtils.getToken();
		TokenUtils.setUserInfo(token, null);
		return ResultUtils.success();
	}
	
	public UserInfo buildUserInfo(User user) {
		if (user == null) {
			return null;
		}
		UserInfo userInfo = new UserInfo();
		BeanUtils.copyProperties(user, userInfo);
		userInfo.setUserId(user.getId());
		return userInfo;
	}
	
}
