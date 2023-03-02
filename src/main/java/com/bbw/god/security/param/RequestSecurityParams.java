package com.bbw.god.security.param;

import lombok.Data;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-13 00:28
 */
@Data
public class RequestSecurityParams {
	private String uri;//请求地址
	private Long uid;//玩家标识
	private String tokenCode;//令牌
	private long timestamp;//请求时间戳
}
