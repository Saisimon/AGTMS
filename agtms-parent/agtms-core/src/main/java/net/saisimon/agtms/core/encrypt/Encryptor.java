package net.saisimon.agtms.core.encrypt;

/**
 * 加解密接口
 * 
 * @author saisimon
 *
 */
public interface Encryptor {
	
	/**
	 * 算法标识
	 * 
	 * @return
	 */
	String algorithm();
	
	/**
	 * 加密
	 * 
	 * @param text 明文
	 * @return 密文
	 */
	String encrypt(String text) throws Exception;
	
	/**
	 * 解密
	 * 
	 * @param ciphertext 密文
	 * @return 明文
	 */
	String decrypt(String ciphertext) throws Exception;
	
}
