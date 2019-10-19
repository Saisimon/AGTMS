package net.saisimon.agtms.web.controller.user;

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
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;
import net.saisimon.agtms.web.dto.req.UserProfileSaveParam;
import net.saisimon.agtms.web.service.main.NavigationMainService;
import net.saisimon.agtms.web.service.main.NotificationMainService;
import net.saisimon.agtms.web.service.user.UserInfoService;

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
	
	@Autowired
	private UserInfoService userInfoService;
	
	/**
	 * 校验用户信息
	 * 
	 * @param param 用户信息
	 * @param result 
	 * @return 用户信息正确返回成功响应，否则返回失败原因响应
	 */
	@Operate(type=OperateTypes.LOGIN)
	@PostMapping("/auth")
	public Result auth(@Validated @RequestBody UserAuthParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		return userInfoService.auth(param);
	}
	
	/**
	 * 获取导航树
	 * 
	 * @return 导航树结果
	 */
	@Operate(type=OperateTypes.QUERY, value=NavigationMainService.NAVIGATION)
	@PostMapping("/nav")
	public Result nav() {
		return userInfoService.nav();
	}
	
	/**
	 * 获取消息通知数量
	 * 
	 * @return 消息通知数量结果
	 */
	@PostMapping("/notification")
	public Result notification() {
		return userInfoService.notification();
	}
	
	@Operate(type=OperateTypes.EDIT, value="read")
	@PostMapping("/notification/read")
	public Result read(@RequestParam(name = "id") Long id) {
		return userInfoService.read(id);
	}
	
	/**
	 * 获取消息通知
	 * 
	 * @return 消息通知结果
	 */
	@Operate(type=OperateTypes.QUERY, value=NotificationMainService.NOTIFICATION)
	@PostMapping("/notifications")
	public Result notifications() {
		return userInfoService.notifications();
	}
	
	/**
	 * 用户修改密码
	 * 
	 * @param param 密码信息
	 * @param result 
	 * @return 密码信息正确返回成功响应，否则返回失败原因响应
	 */
	@Operate(type=OperateTypes.EDIT, value="change.password")
	@PostMapping("/password/change")
	public Result passwordChange(@Validated @RequestBody UserPasswordChangeParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		return userInfoService.passwordChange(param);
	}
	
	/**
	 * 用户个人资料
	 * 
	 * @return 个人资料结果
	 */
	@Operate(type=OperateTypes.QUERY, value="profile")
	@PostMapping("/profile/info")
	public Result profileInfo() {
		return userInfoService.profileInfo();
	}
	
	/**
	 * 用户编辑个人资料
	 * 
	 * @param param 个人资料信息
	 * @param result
	 * @return 个人资料信息正确返回成功响应，否则返回失败原因响应
	 */
	@Operate(type=OperateTypes.EDIT, value="edit.profile")
	@PostMapping("/profile/save")
	public Result profileSave(@RequestBody UserProfileSaveParam param) {
		return userInfoService.profileSave(param);
	}
	
	/**
	 * 用户登出
	 * 
	 * @return 登出结果响应
	 */
	@Operate(type=OperateTypes.LOGOUT)
	@RequestMapping("/logout")
	public Result logout() {
		return userInfoService.logout();
	}
	
}
