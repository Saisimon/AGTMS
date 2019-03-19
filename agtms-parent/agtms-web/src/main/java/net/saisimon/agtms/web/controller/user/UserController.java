package net.saisimon.agtms.web.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
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
	
	@Autowired
	private DiscoveryClient discoveryClient;
	@Autowired(required = false)
	private RemoteService remoteService;
	
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
		remote();
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
		if (!SystemUtils.isEmail(param.getEmail())) {
			return ErrorMessage.User.EMAIL_FORMAT_ERROR;
		}
		UserService userService = UserServiceFactory.get();
		User user = userService.register(param.getName(), param.getEmail(), param.getPassword());
		if (user == null) {
			return ErrorMessage.User.ACCOUNT_ALREADY_EXISTS;
		}
		UserInfo userInfo = buildUserInfo(user);
		userInfo.setToken(AuthUtils.createToken());
		AuthUtils.setUserInfo(userInfo.getUserId().toString(), userInfo);
		remote();
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
	
	private UserInfo buildUserInfo(User user) {
		if (user == null) {
			return null;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(user.getId());
		userInfo.setLoginName(user.getLoginName());
		return userInfo;
	}
	
	private void remote() {
		if (remoteService == null) {
			return;
		}
		Map<String, Template> remoteTemplateMap = new ConcurrentHashMap<>();
		Map<String, Selection> remoteSelectionMap = new ConcurrentHashMap<>();
		List<Template> templates = new ArrayList<>();
		List<String> services = discoveryClient.getServices();
		for (String service : services) {
			if (service.toLowerCase().startsWith("agtms-")) {
				continue;
			}
			List<Template> remoteTemplates = remoteService.templates(service);
			if (CollectionUtils.isEmpty(remoteTemplates)) {
				continue;
			}
			for (Template template : remoteTemplates) {
				template.setService(service);
				templates.add(template);
				String sign = template.sign();
				if (sign != null) {
					remoteTemplateMap.put(sign, template);
				}
				for (TemplateColumn templateColumn : template.getColumns()) {
					for (TemplateField templateField : templateColumn.getFields()) {
						if (Views.SELECTION.getView().equals(templateField.getView())) {
							String selectionSign = templateField.selectionSign(service);
							Selection selection = new Selection();
							selection.setService(service);
							selection.setKey(templateField.getSelection());
							selection.setType(SelectTypes.REMOTE.getType());
							remoteSelectionMap.put(selectionSign, selection);
						}
					}
				}
			}
		}
		TemplateUtils.REMOTE_TEMPLATE_MAP = remoteTemplateMap;
		SelectionUtils.REMOTE_SELECTION_MAP = remoteSelectionMap;
	}
	
}
