package com.bbw.god.security;

import com.bbw.common.HexByteConveter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年5月11日 下午4:11:28
 */
public class Password {
	private static Logger logger = LoggerFactory.getLogger(Password.class);
	/** 富甲封神传使用该字符串生成暗码 **/
	public static final String GOD_PASSWORD_SECRET_KEY = "78524613";
	/**
	 * 默认密码ilovezf的密文
	 */
	public static String ilovezf = "7e24657b262e0216";

	/**
	  * 暗码转明码
	  * @param secretPassword
	  * @return
	  * @throws Exception
	  */
	public static String getLightPassword(String secretPassword) {
		SecretKeySpec keySpec = new SecretKeySpec(GOD_PASSWORD_SECRET_KEY.getBytes(), "DES");
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decByte = HexByteConveter.hex2Byte(secretPassword);
			byte[] result = cipher.doFinal(decByte);
			return new String(result);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 明码转暗码
	 * @param lightPassword
	 * @return
	 * @throws Exception
	 */
	public static String getSecretPassword(String lightPassword) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(GOD_PASSWORD_SECRET_KEY.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] byteFinal = cipher.doFinal(lightPassword.getBytes());
		String strFinal = HexByteConveter.byte2Hex(byteFinal);
		return strFinal;
	}

}
