package net.saisimon.agtms.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.saisimon.agtms.api.hystrix.UserHystrixFallback;

@FeignClient(name = "agtms-web", fallback = UserHystrixFallback.class)
public interface UserInterface {
	
	@PostMapping("/api/check/token")
	boolean checkToken(@RequestParam("uid") String uid, @RequestParam("token") String token);
	
}
