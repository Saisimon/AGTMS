package net.saisimon.agtms.api.hystrix;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.api.UserInterface;

/**
 * 用户接口断路器
 * 
 * @author saisimon
 *
 */
@Slf4j
@Component
public class UserHystrixFallback implements UserInterface {

	@Override
	public boolean checkToken(String uid, String token) {
		log.error("远程调用失败！方法 -> UserInfo checkToken(String uid, String token)");
		return false;
	}

}
