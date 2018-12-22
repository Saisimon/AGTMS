package net.saisimon.agtms.api.hystrix;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.api.UserInterface;

@Slf4j
@Component
public class UserHystrixFallback implements UserInterface {

	@Override
	public boolean checkToken(String token) {
		log.error("远程调用失败！方法 -> UserInfo checkToken(String token)");
		return false;
	}

}
