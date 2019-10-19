package net.saisimon.agtms.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 账户属性字段
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString(exclude = { "resetPassword" })
@ConfigurationProperties(prefix="agtms.account")
public class AccountProperties {
	
	private Account admin = new Account("admin", "123456");
	
	private Account editor = new Account("editor", "editor");
	
	private String resetPassword = "123456";
	
	@Setter
	@Getter
	@ToString(exclude = { "password" })
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Account {
		
		private String username;
		
		private String password;
		
	}
	
}
