package net.saisimon.agtms.web.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.core.util.TokenUtils;

@RestController
@Slf4j
public class TestController {
	
	@PostMapping("/test")
	public void test() throws GenerateException {
		UserService userService = UserServiceFactory.get();
		UserInfo userInfo = buildUserInfo(userService.findById(1L).get());
		TokenUtils.setUserInfo("TEST", userInfo);
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.findById(2L).get();
		TemplateUtils.getRequireds(template);
		log.info(SystemUtils.toJson(template));
		templateService.createTable(template);
//		GenerateService generateService = GenerateServiceFactory.build(template);
//		Domain domain = generateService.newGenerate();
//		domain.setField("column0field0", "用户名", String.class);
//		domain.setField("column0field2", "1990-01-01", String.class);
//		domain.setField("column0field3", "13888888888", String.class);
//		domain.setField("column0field4", "https://www.saisimon.net", String.class);
//		domain.setField("column1field0", "个人简介", String.class);
//		domain.setField("column2field0", "详细简介详细简介详细简介", String.class);
//		generateService.saveDomain(domain);
//		log.info("Count: " + generateService.count(null));
//		List<Domain> domains = generateService.findList(FilterRequest.build().and("column0field0", "用", Operator.REGEX), FilterSort.build("id", Direction.DESC));
//		log.info("Size: " + domains.size());
//		generateService.dropDomains();
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
