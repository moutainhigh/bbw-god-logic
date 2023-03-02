package com.bbw.god.fight.security;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-11 21:44
 */
@Data
public class AttackHistory {
	private Long uid;
	private long version;
	private String last1Ack;
	private String last2Ack;
	private long last1Timestamp = -1;
	private long last2Timestamp = -1;

	public void updateCode(String code) {
		last2Ack = last1Ack;
		last1Ack = code;
	}

	public void updateTimestamp(long timestamp) {
		last2Timestamp = last1Timestamp;
		last1Timestamp = timestamp;
	}
}