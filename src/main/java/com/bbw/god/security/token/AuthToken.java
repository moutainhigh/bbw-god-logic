package com.bbw.god.security.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户权限
 *
 * @author: suhq
 * @date: 2021/11/25 6:07 下午
 */
@Data
@NoArgsConstructor //必须要有一个空构造器，负责重新启动后已登录的玩家将无法获取authtoken
@AllArgsConstructor
public class AuthToken implements Serializable {
	private static final long serialVersionUID = 8722588025315401064L;
	/** 请求token */
	private String token;
	/** 过期时间 */
	private Date expiredDate;
}
