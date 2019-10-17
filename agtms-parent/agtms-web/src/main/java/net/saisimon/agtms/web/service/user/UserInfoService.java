package net.saisimon.agtms.web.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.UserAuthParam;
import net.saisimon.agtms.web.dto.req.UserPasswordChangeParam;
import net.saisimon.agtms.web.dto.req.UserProfileSaveParam;
import net.saisimon.agtms.web.dto.resp.NavigationTree;
import net.saisimon.agtms.web.dto.resp.NavigationTree.NavigationLink;
import net.saisimon.agtms.web.dto.resp.ProfileInfo;
import net.saisimon.agtms.web.dto.resp.UserTokenInfo;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 用户信息服务
 * 
 * @author saisimon
 *
 */
@Service
public class UserInfoService {
	
	@Autowired(required = false)
	private DiscoveryClient discoveryClient;
	@Autowired(required = false)
	private RemoteService remoteService;
	@Autowired
	private BasicProperties basicProperties;
	@Autowired
	private MessageService messageService;
	@Autowired
	private PremissionService premissionService;
	
	@Transactional(rollbackOn = Exception.class)
	public Result auth(UserAuthParam param) {
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
		return ResultUtils.simpleSuccess(buildTokenInfo(user, token));
	}
	
	public Result nav() {
		NavigationTree root = new NavigationTree();
		root.setId("-1");
		Map<String, Integer> roleResourceIdMap = premissionService.getRoleResourceMap(AuthUtils.getUid());
		if (!CollectionUtils.isEmpty(roleResourceIdMap)) {
			Set<Long> resourceIds = roleResourceIdMap.keySet().parallelStream().map(s -> Long.valueOf(s)).collect(Collectors.toSet());
			List<Resource> resources = ResourceServiceFactory.get().getResources(resourceIds);
			root.setChildrens(buildTree(resources, root, ""));
			for (Template template : remoteTemplates()) {
				NavigationLink link = new NavigationLink();
				link.setLink("/management/main/" + template.sign());
				link.setName(template.getTitle());
				root.getLinks().add(link);
			}
		}
		return ResultUtils.simpleSuccess(root);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result passwordChange(UserPasswordChangeParam param) {
		if (param.getOldPassword().length() > 16) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("old.password"), 16);
		}
		if (param.getNewPassword().length() > 16) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("new.password"), 16);
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
	
	@Transactional(rollbackOn = Exception.class)
	public Result profileSave(UserProfileSaveParam param) {
		if (param.getNickname() != null && param.getNickname().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("nickname"), 32);
		}
		if (param.getAvatar() != null && param.getAvatar().length() > 64) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("avatar"), 64);
		}
		if (param.getRemark() != null && param.getRemark().length() > 512) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("remark"), 512);
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
		return token;
	}
	
	private UserTokenInfo buildTokenInfo(User user, UserToken token) {
		UserTokenInfo tokenInfo = new UserTokenInfo();
		tokenInfo.setUserId(user.getId().toString());
		tokenInfo.setExpireTime(token.getExpireTime());
		tokenInfo.setToken(token.getToken());
		tokenInfo.setStatus(user.getStatus());
		tokenInfo.setLoginName(user.getLoginName());
		tokenInfo.setAvatar(user.getAvatar());
		return tokenInfo;
	}
	
	private List<Template> remoteTemplates() {
		List<Template> templates = new ArrayList<>();
		if (remoteService == null || discoveryClient == null) {
			return templates;
		}
		List<String> services = discoveryClient.getServices();
		for (String service : services) {
			if (contains(basicProperties.getExcludeServices(), service)) {
				continue;
			}
			List<Template> remoteTemplates = remoteService.templates(service);
			if (CollectionUtils.isEmpty(remoteTemplates)) {
				continue;
			}
			for (Template template : remoteTemplates) {
				template.setService(service);
				templates.add(template);
			}
		}
		return templates;
	}
	
	private boolean contains(List<String> services, String service) {
		if (services == null) {
			return false;
		}
		for (int i = 0; i < services.size(); i++) {
			if (service.equalsIgnoreCase(services.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	private List<NavigationTree> buildTree(List<Resource> resources, NavigationTree parent, String parentPath) {
		if (CollectionUtils.isEmpty(resources) || parentPath == null) {
			return null;
		}
		List<Resource> currents = new ArrayList<>();
		List<Resource> rests = new ArrayList<>();
		for (Resource res : resources) {
			if (parentPath.equals(res.getPath())) {
				currents.add(res);
			} else {
				rests.add(res);
			}
		}
		List<NavigationTree> trees = new ArrayList<>();
		List<NavigationLink> links = new ArrayList<>();
		for (Resource current : currents) {
			if (SystemUtils.isBlank(current.getLink())) {
				NavigationTree tree = new NavigationTree();
				tree.setId(current.getId().toString());
				tree.setName(messageService.getMessage(current.getName()));
				tree.setIcon(current.getIcon());
				tree.setChildrens(buildTree(rests, tree, current.getPath() + "/" + current.getId()));
				trees.add(tree);
			} else {
				NavigationLink link = new NavigationLink();
				String l = current.getLink();
				if ("/management/main".equals(l)) {
					l += "/" + current.getContentId();
				}
				link.setLink(l);
				link.setName(messageService.getMessage(current.getName()));
				links.add(link);
			}
		}
		parent.setLinks(links);
		return trees;
	}
	
}
