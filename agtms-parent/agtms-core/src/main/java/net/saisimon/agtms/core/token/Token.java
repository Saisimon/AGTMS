package net.saisimon.agtms.core.token;

import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.order.BaseOrder;

/**
 * Token 接口
 * 
 * @author saisimon
 *
 */
public interface Token extends BaseOrder {
	
	/**
	 * 根据用户 ID 获取 Token
	 * 
	 * @param uid 用户 ID
	 * @param update 是否更新过期时间
	 * @return 用户 Token 信息
	 */
	UserToken getToken(Long uid, boolean update);
	
	/**
	 * 根据用户 ID 设置 Token
	 * 
	 * @param uid 用户 ID
	 * @param token 用户 Token 信息
	 */
	void setToken(Long uid, UserToken token);
	
}
