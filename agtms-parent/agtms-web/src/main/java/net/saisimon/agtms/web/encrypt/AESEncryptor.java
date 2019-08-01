package net.saisimon.agtms.web.encrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.encrypt.Encryptor;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.util.AuthUtils;

@Component
public class AESEncryptor implements Encryptor {
	
	@Autowired
	private AgtmsProperties agtmsProperties;
	
	@Override
	public String algorithm() {
		return "AES";
	}

	@Override
	public String encrypt(String text) throws Exception {
		if (text == null) {
			return null;
		}
		return AuthUtils.aesEncrypt(text, agtmsProperties.getEncryptorKey());
	}

	@Override
	public String decrypt(String ciphertext) throws Exception {
		if (ciphertext == null) {
			return null;
		}
		return AuthUtils.aesDecrypt(ciphertext, agtmsProperties.getEncryptorKey());
	}

}
