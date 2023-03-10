package com.bbw.java8;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;

import org.junit.Test;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月28日 上午2:50:37
 */
public class Base64Test {

	@Test
	public void test() {
		try {

			// 使用基本编码
			String base64encodedString = Base64.getEncoder().encodeToString("runoob?java8".getBytes("utf-8"));
			System.out.println("Base64 比那么字符串 (基本) :" + base64encodedString);

			// 解码
			byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);

			System.out.println("原始字符串: " + new String(base64decodedBytes, "utf-8"));
			base64encodedString = Base64.getUrlEncoder().encodeToString("TutorialsPoint?java8".getBytes("utf-8"));
			System.out.println("Base64 编码字符串 (URL) :" + base64encodedString);

			StringBuilder stringBuilder = new StringBuilder();

			for (int i = 0; i < 10; ++i) {
				stringBuilder.append(UUID.randomUUID().toString());
			}

			byte[] mimeBytes = stringBuilder.toString().getBytes("utf-8");
			String mimeEncodedString = Base64.getMimeEncoder().encodeToString(mimeBytes);
			System.out.println("Base64 编码字符串 (MIME) :" + mimeEncodedString);

		} catch (UnsupportedEncodingException e) {
			System.out.println("Error :" + e.getMessage());
		}
	}

}
