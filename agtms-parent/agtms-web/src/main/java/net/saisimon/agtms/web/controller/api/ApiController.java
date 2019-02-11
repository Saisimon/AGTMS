package net.saisimon.agtms.web.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.util.AuthUtils;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	
	@PostMapping("/check/token")
	public boolean checkToken(@RequestParam("uid") String uid, @RequestParam("token") String token) {
		UserInfo userInfo = AuthUtils.getUserInfo(uid);
		if (userInfo == null) {
			return false;
		}
		return token.equals(userInfo.getToken());
	}
	
}
