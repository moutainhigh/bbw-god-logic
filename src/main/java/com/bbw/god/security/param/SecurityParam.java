package com.bbw.god.security.param;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-06 18:10
 */
@Data
public class SecurityParam {
	private Long uid;
	private long version;
	private String lastUri;
	private String last1Code;
	private String last2Code;
	//最近请求的时间
	private long lastTimestamp = -1;
	//	private long last2Timestamp = -1;
	//同一个时间内的请求数
	private int times = 0;
	private Set<String> tokens = new HashSet<>();

	public void updateCode(String code) {
		last2Code = last1Code;
		last1Code = code;
	}

	public void updateTimestamp(long timestamp) {
		if (timestamp != lastTimestamp){
			lastTimestamp = timestamp;
			times = 1;
			return;
		}
		times++;
	}

	public void updateUri(String uri) {
		lastUri = uri;
	}
}
