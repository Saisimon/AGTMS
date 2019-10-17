package net.saisimon.agtms.web.encrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.encrypt.Encryptor;
import net.saisimon.agtms.core.property.EncryptorProperties;
import net.saisimon.agtms.core.util.AuthUtils;

/**
 * AES 对称加密器
 * 
 * @author saisimon
 *
 */
@Component
public class AESEncryptor implements Encryptor {
	
	@Autowired
	private EncryptorProperties encryptorProperties;
	
	@Override
	public String algorithm() {
		return "AES";
	}

	@Override
	public String encrypt(String text) throws Exception {
		if (text == null) {
			return null;
		}
		return AuthUtils.aesEncrypt(text, encryptorProperties.getSecret());
	}

	@Override
	public String decrypt(String ciphertext) throws Exception {
		if (ciphertext == null) {
			return null;
		}
		return AuthUtils.aesDecrypt(ciphertext, encryptorProperties.getSecret());
	}

}
