package com.bbw.god.uac.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.common.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 玩家账号
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-23 11:12:27
 */
@Data
@TableName("godmanager.account")
public class AccountEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Integer id; //
	private Integer plat; //渠道
	private String email; //账号标识
	private String openId; //微信openid
	private String password; //密码（MD5加密方式）
	private String token; //
	private Date enrollTime; //注册时间
	private String invitationCode; //邀请码
	private Integer forgetTimes; //
	private Date lastForgetTime; //
	private String adId; //
	private String idFa; //
	private Date lastLoginTime; //上次登录时间
	private Date rowUpdateTime = new Date(); //表数据更新时间
	private Integer userSrc = 1; //用户来源。1:自然流量。3:买量用户
	private Integer regDate = DateUtil.toDateInt(DateUtil.now()); //注册日期

	public AccountEntity() {
		enrollTime = DateUtil.now();
	}

	@Getter
	@AllArgsConstructor
	public enum UserSrc {
		Bamboowind(100), //竹风
		Channel(200),
		Wechat(200);//微信
		private int value;
	}

}
