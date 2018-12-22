package net.saisimon.agtms.web.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.util.TokenUtils;

@RestController
@RequestMapping(path = "/api")
public class ApiController {
	
	@PostMapping("/check/token")
	public boolean checkToken(@RequestParam("token") String token) {
		return TokenUtils.getUserInfo(token) != null;
	}
	
}
