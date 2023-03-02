package com.bbw.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.HexBin;

/**
 * 密码工具
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年5月11日 下午4:11:28
 */
public class MD5Tool {
	private static Logger logger = LoggerFactory.getLogger(MD5Tool.class);

	/**
	 * md5加密
	 * @param sourceStr
	 * @return
	 */
	public static String md5Encode(String sourceStr) {
		String signStr = null;
		try {
			byte[] bytes = sourceStr.getBytes("utf-8");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] md5Byte = md5.digest();
			if (md5Byte != null) {
				signStr = HexBin.encode(md5Byte);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		return signStr;

	}

}
