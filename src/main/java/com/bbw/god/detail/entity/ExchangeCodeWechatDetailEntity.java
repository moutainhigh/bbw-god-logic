package com.bbw.god.detail.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

/**
 * 礼包兑换记录
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-10-28 11:42:10
 */
@Data
@TableName("god_detail.exchange_code_wechat_detail")
public class ExchangeCodeWechatDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private String id; //code,gameuserid,serverid,validtime的组合
	private String user; //玩家。account.email
	private Integer serverId; //服务器ID
	private Long gameuserId; //区服玩家ID
	private String code; //礼包码
	private Integer packs; //礼包。packs.id
	private Boolean status; //状态。1已领取，0未领取
	private Date validTime = new Date(); //截止时间
	private Date dispatchTime; //生效时间
}
