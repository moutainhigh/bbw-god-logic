package com.bbw.god.uac.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 账号绑定
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-23 11:15:39
 */
@Data
@TableName("godmanager.goduc_account_bind")
public class AccountBindEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Integer id; //
	private String playerAccount; //
	private String bindKey; //绑定账号的唯一标识
	private Integer bindType; //绑定类型。1微信；2手机号；4支付宝；
	private Date opTime = new Date(); //最近更新时间
	private String bindName; //绑定的名称
}
